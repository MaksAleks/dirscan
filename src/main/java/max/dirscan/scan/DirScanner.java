package max.dirscan.scan;

import max.dirscan.output.FilesProcessor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class DirScanner {

    private List<Path> dirForScan;
    private ForkJoinPool scanPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    public DirScanner(List<String> dirForScan) {
        this.dirForScan = dirForScan.stream()
                .map(Paths::get)
                .collect(Collectors.toList());
    }

    public void scan() {
        System.out.println("scanning...");
        dirForScan.stream()
                .map(DirScanning::new)
                .forEach(scanPool::invoke);

        System.out.println("sorting...");
        FilesProcessor.getProcessor().finish();
        while (scanPool.hasQueuedSubmissions() || scanPool.getActiveThreadCount() > 0) {
            Thread.yield();
        }
        scanPool.shutdown();
    }
}
