package max.dirscan.output;


import max.dirscan.config.OutputWriterConfig;
import max.dirscan.output.buffer.SortedOutputEntriesBuffer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.nio.file.*;

//Singleton
public abstract class OutputEntryFileWriter {

    private OutputWriterConfig config;

    private LinkedBlockingQueue<OutputEntry> outputQueue = new LinkedBlockingQueue<>(Runtime.getRuntime().availableProcessors());

    private ForkJoinPool outputEntriesPool = new ForkJoinPool();

    private SortedOutputEntriesBuffer entriesBuffer;

    private BufferFiller bufferFiller;

    public OutputEntryFileWriter(OutputWriterConfig config) {
        this.config = config;
        this.entriesBuffer = new SortedOutputEntriesBuffer(config.bufferSize());
        bufferFiller = new BufferFiller();
        CompletableFuture.runAsync(bufferFiller);
    }

    protected abstract void flushEntry(OutputEntry entry, Path file);

    public void writeOutputEntry(OutputEntry outputEntry) {
        putOutputEntry(outputEntry);
    }

    public void performSorting() {
        if(bufferFiller.flushCounter == 1) {
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

    private class BufferFiller implements Runnable {

        private int flushCounter = 0;

        @Override
        public void run() {
            try {
                while (true) {
                    OutputEntry outputEntry = outputQueue.take();
                    if (entriesBuffer.put(outputEntry) < 0) {
                        String filePath = config.outputFilePath();
                        if(flushCounter > 0) {
                            filePath = filePath +  "_" + flushCounter;
                        }
                        Path file = Paths.get(filePath);
                        entriesBuffer.flush((entry, path) -> flushEntry(entry, path), file);
                        entriesBuffer.put(outputEntry);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
