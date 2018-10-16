package max.dirscan.scan;

import max.dirscan.output.FilesProcessor;
import max.dirscan.scan.filter.DirExcludeFilter;
import max.dirscan.scan.filter.ExcludeFilter;
import max.dirscan.scan.filter.FileExcludeFilter;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class DirScanner {

    private List<Path> dirForScan;
    private List<DirExcludeFilter> dirExcludeFilters = new LinkedList<>();
    private List<FileExcludeFilter> fileExcludeFilters = new LinkedList<>();

    private ForkJoinPool scanPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    public DirScanner(List<Path> dirForScan) {
        this.dirForScan = dirForScan;
    }


    public void registerFilters(List<ExcludeFilter> filters) {
        filters.forEach(this::registerFilter);
    }

    public void registerFilter(ExcludeFilter filter) {
        if(filter instanceof DirExcludeFilter) {
            DirExcludeFilter dirExcludeFilter = (DirExcludeFilter) filter;
            dirExcludeFilters.add(dirExcludeFilter);
        } else if (filter instanceof FileExcludeFilter) {
            FileExcludeFilter fileExcludeFilter = (FileExcludeFilter)filter;
            fileExcludeFilters.add(fileExcludeFilter);
        } else {
            throw new IllegalArgumentException("Filter has unknown type");
        }
    }

    public void scan() {
        System.out.println("scanning...");
        dirForScan.stream()
                .map(dir -> new DirScanning(dir, dirExcludeFilters, fileExcludeFilters))
                .forEach(scanPool::invoke);

        System.out.println("sorting...");
        FilesProcessor.getProcessor().finish();
        while (scanPool.hasQueuedSubmissions() || scanPool.getActiveThreadCount() > 0) {
            Thread.yield();
        }
        scanPool.shutdown();
    }
}
