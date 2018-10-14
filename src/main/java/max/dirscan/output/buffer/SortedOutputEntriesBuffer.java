package max.dirscan.output.buffer;


import max.dirscan.output.OutputEntry;

import java.nio.file.Path;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class SortedOutputEntriesBuffer {

    private int size = 0;

    private int currentSize = 0;

    private SortedSet<OutputEntry> entries;

    public SortedOutputEntriesBuffer(int size) {
        this.size = size;
        entries = new TreeSet<>();
    }

    public int put(OutputEntry entry) {
        if(currentSize == size) {
            return -1;
        } else {
            entries.add(entry);
            return ++currentSize;
        }
    }


    public void flush(BiConsumer<OutputEntry, Path> consumer, Path file) {
        currentSize = 0;
        for(OutputEntry entry: entries) {
            consumer.accept(entry, file);
        }
    }

    public void flushAsync(BiConsumer<OutputEntry, Path> consumer, Path file) {
        CompletableFuture.runAsync(() -> flush(consumer, file));
    }

    public int getCurrentSize() {
        return currentSize;
    }
}
