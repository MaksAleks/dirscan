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

    public static void main(String[] args) {

        String s1 = "/home/maxim/.IntelliJIdea2016.3/config/tasks/RISKI_TEST.tasks.zip";
        String s2 = "/home/maxim/.IntelliJIdea2016.3/config/tasks/Riski.contexts.zip";

        SortedFilesBuffer buffer = new SortedFilesBuffer(5000);
        buffer.put(s1);
        buffer.put(s2);

        buffer.takeAll().forEach(System.out::println);
    }
}
