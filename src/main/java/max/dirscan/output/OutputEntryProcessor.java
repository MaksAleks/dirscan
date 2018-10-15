package max.dirscan.output;


import max.dirscan.config.OutputEntryWritingConfig;
import max.dirscan.output.writer.OutputEntryWriter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.SortedSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

// singleton
public class OutputEntryProcessor {

    private boolean isInit = false;

    private LinkedBlockingQueue<OutputEntry> outputEntriesQueue = new LinkedBlockingQueue<>();

    private ForkJoinPool queueFiller = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    private Thread writerManagerThread;

    private OutputEntryWriterManager writerManager;

    private OutputEntryProcessor() {

    }

    private static OutputEntryProcessor processor = new OutputEntryProcessor();

    public static OutputEntryProcessor getProcessor() {
        return processor;
    }

    public void init(OutputEntryWritingConfig config) {
        writerManager = new OutputEntryWriterManager(config);
        writerManagerThread = new Thread(writerManager);
        writerManagerThread.start();
        isInit = true;
    }

    public boolean isInit() {
        return isInit;
    }

    public void process(Path file, BasicFileAttributes attrs) {
        CompletableFuture.runAsync(() -> putOutputEntry(file, attrs), queueFiller);
    }

    public void performSorting() {
        while (queueFiller.hasQueuedSubmissions() || queueFiller.getActiveThreadCount() > 0) {
            Thread.yield();
        }
        queueFiller.shutdown();
        writerManagerThread.interrupt();
        if (writerManager.flushCounter == 1) {
            return;
        }
    }

    private void putOutputEntry(Path file, BasicFileAttributes attrs) {
        try {
            OutputEntry entry = OutputEntry.builder()
                    .path(file)
                    .attrs(attrs)
                    .build();
            outputEntriesQueue.put(entry);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private class OutputEntryWriterManager implements Runnable {

        private int flushCounter = 0;

        private SortedOutputEntriesBuffer entriesBuffer;

        private OutputEntryWritingConfig config;

        public OutputEntryWriterManager(OutputEntryWritingConfig config) {
            this.config = config;
            this.entriesBuffer = new SortedOutputEntriesBuffer(config.outputEntryBufferSize());
        }

        @Override
        public void run() {

            Long start = System.currentTimeMillis();
            while (true) {
                try {
                    OutputEntry outputEntry = outputEntriesQueue.take();
                    if (entriesBuffer.put(outputEntry) < 0) {
                        flushBuffer();
                        entriesBuffer.put(outputEntry);
                    }
                } catch (InterruptedException e) {
                    System.out.println("finishing buffer filler..");
                    finish();
                    System.out.println("Writing took = " + (System.currentTimeMillis() - start));
                    return;
                }
            }
        }

        private void finish() {
            try {
                while (true) {
                    OutputEntry outputEntry = outputEntriesQueue.poll();
                    if (outputEntry == null) {
                        if (entriesBuffer.getCurrentSize() > 0) {
                            flushBuffer();
                        }

                        if (flushCounter == 1) {
                            Files.move(Paths.get(config.outputFilePath() + "_0"), Paths.get(config.outputFilePath()));
                            return;
                        }
                    } else {
                        if (entriesBuffer.put(outputEntry) < 0) {
                            flushBuffer();
                            entriesBuffer.put(outputEntry);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void flushBuffer() {
            String filePath = config.outputFilePath();
            filePath = filePath + "_" + flushCounter;
            flushCounter++;
            Path file = Paths.get(filePath);
            SortedSet<OutputEntry> toFlush = entriesBuffer.takeAll();
            flushToFile(file, toFlush);
        }

        private void flushToFile(Path file, SortedSet<OutputEntry> toFlush) {
            OutputEntryWriter writer = getEntryWriter(file);
            toFlush.forEach(writer::writeEntry);
        }

        private OutputEntryWriter getEntryWriter(Path path) {
            try {
                Constructor<? extends OutputEntryWriter> constructor = config.outputEntryWriterClass().getConstructor(Path.class, OutputEntryWritingConfig.class);
                return constructor.newInstance(path, config);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
