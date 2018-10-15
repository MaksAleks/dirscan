package max.dirscan.output;


import max.dirscan.config.OutputEntryProcessorConfig;
import max.dirscan.output.buffer.SortedOutputEntriesBuffer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.LinkedBlockingQueue;

// singleton
public class OutputEntryProcessor {

    private boolean isInit = false;

    private OutputEntryProcessorConfig config;

    private LinkedBlockingQueue<OutputEntry> outputQueue = new LinkedBlockingQueue<>();

    private SortedOutputEntriesBuffer entriesBuffer;

    private Thread bufferFillerThread;

    private BufferFiller bufferFiller;

    private OutputEntryProcessor() {

    }

    private static OutputEntryProcessor processor = new OutputEntryProcessor();

    public static OutputEntryProcessor getProcessor() {
        return processor;
    }

    public void init(OutputEntryProcessorConfig config) {
        this.config = config;
        this.entriesBuffer = new SortedOutputEntriesBuffer(config.bufferSize());
        bufferFiller = new BufferFiller();
        bufferFillerThread = new Thread(bufferFiller);
        bufferFillerThread.start();
        isInit = true;
    }

    public boolean isInit() {
        return isInit;
    }

    public void process(OutputEntry outputEntry) {
        putOutputEntry(outputEntry);
    }

    public void performSorting() {
        bufferFillerThread.interrupt();
        if (bufferFiller.flushCounter == 1) {
            return;
        }
    }

    private void putOutputEntry(OutputEntry outputEntry) {
        try {
            outputQueue.put(outputEntry);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private OutputEntryWriter getEntryWriter(Path path) {
        try {
            Constructor<? extends OutputEntryWriter> constructor = config.getWriterClass().getConstructor(Path.class, OutputEntryProcessorConfig.class);
            return constructor.newInstance(path, config);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private class BufferFiller implements Runnable {

        private int flushCounter = 0;

        @Override
        public void run() {
            while (true) {
                try {
                    OutputEntry outputEntry = outputQueue.take();
                    if (entriesBuffer.put(outputEntry) < 0) {
                        flushBuffer();
                        entriesBuffer.put(outputEntry);
                    }
                } catch (InterruptedException e) {
                    System.out.println("finishing buffer filler..");
                    System.out.println("queue size = " + outputQueue.size());
                    finish();
                    return;
                }
            }
        }

        private void finish() {
            while (true) {
                OutputEntry outputEntry = outputQueue.poll();
                if(outputEntry == null) {
                    if(entriesBuffer.getCurrentSize() > 0) {
                        System.out.println("queue is empty... last flush");
                        flushBuffer();
                        return;
                    }
                } else {
                    if (entriesBuffer.put(outputEntry) < 0) {
                        System.out.println("buffer is full - flush");
                        flushBuffer();
                        entriesBuffer.put(outputEntry);
                    }
                }
            }
        }

        private void flushBuffer() {
            OutputEntryWriter writer;
            String filePath = config.outputFilePath();
            if (flushCounter++ > 0) {
                filePath = filePath + "_" + flushCounter;
            }
            Path file = Paths.get(filePath);
            writer = getEntryWriter(file);
            entriesBuffer.flush(writer::writeEntry);
        }

    }
}
