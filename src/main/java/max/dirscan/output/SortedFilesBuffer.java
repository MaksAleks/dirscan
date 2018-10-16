package max.dirscan.output;


import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class SortedFilesBuffer {

    private int maxSize;

    private int countOfElements = 0;

    private long currentSize = 0;

    private List<String> lines;

    public SortedFilesBuffer(int maxSize) {
        this.maxSize = maxSize;
        lines = new LinkedList<>();
    }

    public long put(String line) {
        long lineSize = line.getBytes(StandardCharsets.UTF_8).length;
        if (currentSize + lineSize > maxSize) {
            return -1;
        } else {
            lines.add(line);
            countOfElements++;
            return currentSize += lineSize;
        }
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public List<String> takeAll() {
        List<String> result = new LinkedList<>(lines);
        result.sort(String::compareToIgnoreCase);
        lines = new LinkedList<>();
        currentSize = 0;
        countOfElements = 0;
        return result;
    }
}
