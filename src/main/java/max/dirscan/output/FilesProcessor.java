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

    public void init(ApplicationConfig config) {
        writerManager = new WriteManager(config);
        writerManagerThread = new Thread(writerManager);
        writerManagerThread.setName("WriterManager Thread");
        isInit = true;
    }

    public boolean isStared() {
        return isStared;
    }

    public void start() {
        if(!isInit) {
            throw new InitException("Cannot start File Processor: File Processor is not initialized");
        }
        writerManagerThread.start();
        isStared = true;
    }

    public void process(String fileName) {
        CompletableFuture.runAsync(() -> writerManager.putOutputEntry(fileName), queueFiller);
    }

    public void finish() {
        while (queueFiller.hasQueuedSubmissions() || queueFiller.getActiveThreadCount() > 0 || writerManager.filesQueue.size() > 0) {
            Thread.yield();
        }
        writerManagerThread.interrupt();
    }

    public void waitForComplete() {
        while (isStared) {
            Thread.yield();
        }
    }

    private class WriteManager implements Runnable {

        private int flushCounter = 0;

        private ApplicationConfig config;

        private SortedFilesBuffer buffer;

        private List<File> sortedFiles = new ArrayList<>();

        private LinkedBlockingQueue<String> filesQueue = new LinkedBlockingQueue<>();

        public WriteManager(ApplicationConfig config) {
            this.config = config;
            this.buffer = new SortedFilesBuffer(config.outputEntryBufferSize());
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String file = filesQueue.take();
                    if (buffer.put(file) < 0) {
                        flushBuffer();
                        buffer.put(file);
                    }
                } catch (InterruptedException e) {
                    finish();
                    return;
                }
            }
        }

        private void finish() {
            try {
                while (true) {
                    String file = filesQueue.poll();
                    if (file == null) {
                        if (buffer.getCurrentSize() > 0) {
                            flushBuffer();
                        }
                        if (flushCounter == 1) {
                            Files.move(Paths.get(config.outputFilePath() + "_0"), Paths.get(config.outputFilePath()));
                        } else {
                            performSorting();
                        }
                        FileFormatter formatter = config.fileFormatter();
                        formatter.format();
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

        private void performSorting() throws IOException {
            ExternalMergeSort sort = new ExternalMergeSort();
            sort.mergeSortedFiles(
                    sortedFiles,
                    Paths.get(config.outputFilePath()).toFile(),
                    Comparator.comparing(String::toLowerCase),
                    config.outputFileCharset(), true
            );
        }

        private void flushBuffer() {
            String filePath = config.outputFilePath();
            filePath = filePath + "_" + flushCounter;
            flushCounter++;
            Path file = Paths.get(filePath);
            sortedFiles.add(file.toFile());
            List<String> toFlush = buffer.takeAll();
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
