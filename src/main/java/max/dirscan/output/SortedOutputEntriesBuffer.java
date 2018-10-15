package max.dirscan.output;


import java.util.SortedSet;
import java.util.TreeSet;

public class SortedOutputEntriesBuffer {

    private int maxSize;

    private int currentSize = 0;

    private SortedSet<OutputEntry> entries;

    public SortedOutputEntriesBuffer(int maxSize) {
        this.maxSize = maxSize;
        entries = new TreeSet<>();
    }

    public int put(OutputEntry entry) {
        if(currentSize == maxSize) {
            return -1;
        } else {
            entries.add(entry);
            return ++currentSize;
        }
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public SortedSet<OutputEntry> takeAll() {
        SortedSet<OutputEntry> toFlush = new TreeSet<>(entries);
        entries = new TreeSet<>();
        currentSize = 0;
        return toFlush;
    }
}
