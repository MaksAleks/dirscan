package max.dirscan.output;


import max.dirscan.config.OutputEntryWritingConfig;
import max.dirscan.output.format.FileFormatter;

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

import static max.dirscan.sort.ExternalTwoWaySort.mergeSortedFiles;

// singleton
public class FilesProcessor {

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

    public void init(OutputEntryWritingConfig config) {
        writerManager = new WriteManager(config);
        writerManagerThread = new Thread(writerManager);
        writerManagerThread.start();
        isInit = true;
    }

    public boolean isInit() {
        return isInit;
    }

    public void process(String fileName) {
        CompletableFuture.runAsync(() -> writerManager.putOutputEntry(fileName), queueFiller);
    }

    public void finish() {
        while (queueFiller.hasQueuedSubmissions() || queueFiller.getActiveThreadCount() > 0) {
            Thread.yield();
        }
        queueFiller.shutdown();
        writerManagerThread.interrupt();
    }


    private class WriteManager implements Runnable {

        private int flushCounter = 0;

        private OutputEntryWritingConfig config;

        private SortedFilesBuffer buffer;

        private List<File> sortedFiles = new ArrayList<>();

        private LinkedBlockingQueue<String> filesQueue = new LinkedBlockingQueue<>();

        public WriteManager(OutputEntryWritingConfig config) {
            this.config = config;
            this.buffer = new SortedFilesBuffer(config.outputEntryBufferSize());
        }

        @Override
        public void run() {

            Long start = System.currentTimeMillis();
            while (true) {
                try {
                    String file = filesQueue.take();
                    if (buffer.put(file) < 0) {
                        flushBuffer();
                        buffer.put(file);
                    }
                } catch (InterruptedException e) {
                    System.out.println("finishing write manager..");
                    finish();
                    System.out.println("Writing took = " + (System.currentTimeMillis() - start));
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
                        FileFormatter formatter = config.entryFormatter();
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
            }
        }

        private void performSorting() throws IOException {
            mergeSortedFiles(
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
