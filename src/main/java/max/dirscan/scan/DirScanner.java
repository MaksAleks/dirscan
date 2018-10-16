package max.dirscan.scan;

import max.dirscan.output.FilesProcessor;
import max.dirscan.scan.filter.DirFilter;
import max.dirscan.scan.filter.FileFilter;
import max.dirscan.scan.filter.ScanFilter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class DirScanner {

    private List<Path> dirForScan;
    private List<DirFilter> dirFilters = new LinkedList<>();
    private List<FileFilter> fileFilters = new LinkedList<>();

    private ForkJoinPool scanPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    public DirScanner(List<String> dirForScan) {
        this.dirForScan = dirForScan.stream()
                .map(Paths::get)
                .collect(Collectors.toList());
    }


    public void registerFilters(List<ScanFilter> filters) {
        filters.forEach(this::registerFilter);
    }

    public void registerFilter(ScanFilter filter) {
        if(filter instanceof DirFilter) {
            DirFilter dirFilter = (DirFilter) filter;
            dirFilters.add(dirFilter);
        } else if (filter instanceof FileFilter) {
            FileFilter fileFilter = (FileFilter)filter;
            fileFilters.add(fileFilter);
        } else {
            throw new IllegalArgumentException("Filter has unknown type");
        }
    }

    public void scan() {
        System.out.println("scanning...");
        dirForScan.stream()
                .map(dir -> new DirScanning(dir, dirFilters, fileFilters))
                .forEach(scanPool::invoke);

        System.out.println("sorting...");
        FilesProcessor.getProcessor().finish();
        while (scanPool.hasQueuedSubmissions() || scanPool.getActiveThreadCount() > 0) {
            Thread.yield();
        }
        scanPool.shutdown();
    }
}
