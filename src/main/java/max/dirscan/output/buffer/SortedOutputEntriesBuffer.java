package max.dirscan.output.buffer;


import max.dirscan.output.OutputEntry;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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


    public void flush(Consumer<OutputEntry> consumer) {
        currentSize = 0;
        for(OutputEntry entry: entries) {
            consumer.accept(entry);
        }
    }

    public void flushAsync(Consumer<OutputEntry> consumer) {
        CompletableFuture.runAsync(() -> flush(consumer));
    }

    public int getCurrentSize() {
        return currentSize;
    }
}
