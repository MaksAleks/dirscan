package max.dirscan.output;


import max.dirscan.config.ApplicationConfig;
import max.dirscan.exceptions.InitException;
import max.dirscan.output.format.FileFormatter;
import max.dirscan.sort.ExternalMergeSort;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

// singleton

/**
 * Файл отвечающий за обработку найденных файлов
 * Singleton
 */
public class FilesProcessor {

    private volatile boolean isStared = false;

    private boolean isInit = false;

    private ForkJoinPool queueFiller = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    private Thread writerManagerThread;

    private WriteManager writerManager;

    private FilesProcessor() {

    }

    private static FilesProcessor processor = new FilesProcessor();

    public static FilesProcessor getProcessor() {
        return processor;
    }

    /**
     * Метод инициализации
     * происходит создание класса {@link WriteManager} типа Runnable
     * который отвечает за формирование отсортированного файла с
     * абсолютными путями до всех найденных во время сканирования файлов
     * @param config -
     */
    public void init(ApplicationConfig config) {
        writerManager = new WriteManager(config);
        writerManagerThread = new Thread(writerManager);
        writerManagerThread.setName("WriterManager Thread");
        isInit = true;
    }

    public boolean isStared() {
        return isStared;
    }

    /**
     * Метод запуска класса {@link WriteManager}
     * При этом начинается обработка найденных файлов
     */
    public void start() {
        if(!isInit) {
            throw new InitException("Cannot start File Processor: File Processor is not initialized");
        }
        writerManagerThread.start();
        isStared = true;
    }

    /**
     * Метод, который вызывается потоками-сканерами когда они находят очередной файл
     * Строка с путем до найденного файла помещается в блокирующую очередь
     * Из которой {@link WriteManager} забирает файлы и кладёт их в буфер {@link SortedFilesBuffer}
     * @param fileName - абсолюьный путь до найденного файла
     */
    public void process(String fileName) {
        // Т.к. очередь блокирующая - класть туда строки лучше в отдельном потоке,
        // чтобы потоки, отвечающие за поиск файлов не блокировались
        CompletableFuture.runAsync(() -> writerManager.putOutputEntry(fileName), queueFiller);
    }

    /**
     *  Метод сигнализирующий классу {@link WriteManager} о том, что поиск файлов закончился
     *  И если он ничего не сможет найти в очереди, то пусть завершает свою работу
     */
    public void finish() {
        // Дожидаемся что в пуле потоков, которые отвечают за пополнение очереди, не осталось октивных потоков
        // и что очередь пуста и посылаем сигнал WriterManager-у
        while (queueFiller.hasQueuedSubmissions() || queueFiller.getActiveThreadCount() > 0 || writerManager.filesQueue.size() > 0) {
            Thread.yield();
        }
        writerManagerThread.interrupt();
    }

    /**
     * Метод ожидания завершения работы {@link WriteManager}
     */
    public void waitForComplete() {
        while (isStared) {
            Thread.yield();
        }
    }

    /**
     * Класс отечающий за обработку найденных файлов
     */
    private class WriteManager implements Runnable {

        private int flushCounter = 0;

        private ApplicationConfig config;

        // Т.к. найденных файлов может быть много, нужен буфер для хранения
        // такого количества файлов, которые могут поместиться в ОЗУ
        // размер (задается в Конфигурации приложения)
        private SortedFilesBuffer buffer;

        // Список сформированных отсортированных файлов, размер которых не превышает
        // максимального размера буфера
        private List<File> sortedFiles = new ArrayList<>();

        // блокирующая очередь куда складываются пути до найденных файлов
        private LinkedBlockingQueue<String> filesQueue = new LinkedBlockingQueue<>();

        public WriteManager(ApplicationConfig config) {
            this.config = config;
            this.buffer = new SortedFilesBuffer(config.outputEntryBufferSize());
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // Берем из очереди очередной файл (строку с абсолютным путем)
                    // ожидая, если очередь пуста
                    String file = filesQueue.take();
                    if (buffer.put(file) < 0) {
                        // Если при попытке сложить файл в буфер оказалось, что он переполнен
                        // сбасываем буфер во временный отсортированный файл
                        flushBuffer();
                        buffer.put(file);
                    }
                } catch (InterruptedException e) {
                    finish();
                    return;
                }
            }
        }

        /**
         * Метод вызывается когда сканирование завершено.
         * Это сигнал о том, что больше файлы в очередь добавляться не будут
         */
        private void finish() {
            try {
                while (true) {
                    // Забираем оставшиеся в очереди файлы
                    String file = filesQueue.poll();
                    if (file == null) {
                        // Когда находим в очереди null (т.е. очередь пуста)
                        // формируем временный файл, в котором будут храниться все отсортированные по
                        // алфавиту записи
                        Path sortedFile = Paths.get(config.outputFilePath().toString() + "_sorted");
                        Files.deleteIfExists(sortedFile);
                        if (buffer.getCurrentSize() > 0) {
                            // Если в буфере при этом что-то осталось -
                            // сбрасываем остатки в очередной временный отсортированный файл
                            flushBuffer();
                        }
                        if (flushCounter == 1) {

                            // Если оказалось, что мы сбрасывали в буфер только один раз
                            // (т.е. у нас только один временный отсортированный файл)
                            // Он называется как ИмяВыходногоФайла_0 (смотри метод WriterManager.flushBuffer() )
                            String temp0 = config.outputFilePath().toString() + "_0";
                            // Так вот если временный файл только один - переименовываем его в ИмяВыходногоФайла_sorted
                            Files.move(Paths.get(temp0), sortedFile);
                        } else {

                            // Если же отсортированных временных файлов у нас несколько
                            // Нужно их объединить, сохраняя алфавитный порядок
                            performSortingTo(sortedFile);
                        }
                        FileFormatter formatter = config.fileFormatter();
                        formatter.format(sortedFile);
                        return;
                    } else {
                        if (buffer.put(file) < 0) {
                            flushBuffer();
                            buffer.put(file);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                isStared = false;
            }
        }

        /**
         * Метод объединения нескольких отсортированных файлов в один
         * по алгоритму сортировки расширенным слиянием
         * @param sortedFile - файл, куда будет записан результат
         * @throws IOException
         */
        private void performSortingTo(Path sortedFile) throws IOException {
            ExternalMergeSort sort = new ExternalMergeSort();
            sort.mergeSortedFiles(
                    sortedFiles,
                    sortedFile.toAbsolutePath().toFile(),
                    Comparator.comparing(String::toLowerCase),
                    config.outputFileCharset(), true
            );
        }

        /**
         * Метод сброса буфера и формирование временного отсортированного файла
         */
        private void flushBuffer() {
            String filePath = config.outputFilePath().toString();
            filePath = filePath + "_" + flushCounter;
            flushCounter++;
            Path file = Paths.get(filePath);
            sortedFiles.add(file.toFile());
            // Забираем из буфера все пути до файлов (они отсортированны по алфавиту)
            List<String> toFlush = buffer.takeAll();
            //И записываем во временный файл
            flushToFile(file, toFlush);
        }

        private void flushToFile(Path file, List<String> toFlush) {
            FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8);
            writer.writelnLines(toFlush);
        }

        private void putOutputEntry(String fileName) {
            try {
                filesQueue.put(fileName);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
