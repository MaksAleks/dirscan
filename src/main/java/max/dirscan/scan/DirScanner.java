package max.dirscan.scan;

import max.dirscan.output.OutputEntryProcessor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class DirScanner {

    private List<Path> dirForScan;
    private ForkJoinPool scanPool = new ForkJoinPool();

    public DirScanner(List<String> dirForScan) {
        this.dirForScan = dirForScan.stream()
                .map(Paths::get)
                .collect(Collectors.toList());
    }

    public void scan() {
        System.out.println("scanning...");
        dirForScan.parallelStream()
                .map(RecursiveFileTreeWalk::new)
                .forEach(scanPool::invoke);

        System.out.println("starts sorting...");
        OutputEntryProcessor.getProcessor().performSorting();
    }
}
