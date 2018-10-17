package max.dirscan.scan;

import max.dirscan.exceptions.InitException;
import max.dirscan.output.FilesProcessor;
import max.dirscan.scan.filter.DirExcludeFilter;
import max.dirscan.scan.filter.FileExcludeFilter;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

/**
 * Таска представляющее собой непосредственное сканирование директории
 * Каждая таска сканирует только одну директорию
 */
public class DirScanning extends RecursiveAction {

    // Директория для сканирования
    private final Path dir;

    private FilesProcessor processor;

    // Фильтры директорий
    private List<DirExcludeFilter> dirExcludeFilters;

    // Фидльтры файлов
    private List<FileExcludeFilter> fileExcludeFilters;

    public DirScanning(Path dir, List<DirExcludeFilter> dirExcludeFilters, List<FileExcludeFilter> fileExcludeFilters) {
        this.dir = dir;
        this.dirExcludeFilters = dirExcludeFilters;
        this.fileExcludeFilters = fileExcludeFilters;
        processor = FilesProcessor.getProcessor();
        if(!processor.isStared()) {
            throw new InitException("Cannot start scanning: File Processor hasn't been started yet");
        }
    }

    /**
     * Основной метод представляющий собой сканирование
     */
    @Override
    protected void compute() {
        final List<DirScanning> scanningList = new ArrayList<>();
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                // Перед тем как начать сканировать очередную директорию выполняется этот метод
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    // Фильтруем директорию
                    boolean skip = dirExcludeFilters.stream().anyMatch(f -> f.filter(dir));
                    if(skip) {
                        // Если отфильтровалась - пропускаем
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    // Если директория не отфильтровалась, то
                    // Проверяем нужно ли нам её сканировать
                    if (!dir.equals(DirScanning.this.dir)) {
                        // Если сканировать нам не нужно, то создаем новую дочернюю таску для сканирования
                        DirScanning w = new DirScanning(dir, dirExcludeFilters, fileExcludeFilters);
                        // И запускаем её в том же ForkJoinPool
                        w.fork();
                        scanningList.add(w);
                        return FileVisitResult.SKIP_SUBTREE;
                    } else {
                        // Если эта директория для нас - сканируем
                        return FileVisitResult.CONTINUE;
                    }
                }

                // Перед тем как сканировать очередной файл выполняется этот метод
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    //Фильтруем файл
                    boolean process = fileExcludeFilters.stream().noneMatch(f -> f.filter(file));
                    if(process) {
                        //Если не отфильтровался - передаем его FileProcessor'у в очередь
                        processor.process(file.toAbsolutePath().toString());
                    }
                    // И идём дальше
                    return FileVisitResult.CONTINUE;
                }

                // Если возникает ошибка при посещении файла (например нет прав на чтение)- пропускаем
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Дожидаемся пока все дочерние таски закончат работу
        for (DirScanning w : scanningList) {
            w.join();
        }
    }
}
