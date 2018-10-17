package max.dirscan.scan;

import max.dirscan.exceptions.InitException;
import max.dirscan.input.ParseResult;
import max.dirscan.output.FilesProcessor;
import max.dirscan.scan.filter.DirExcludeFilter;
import max.dirscan.scan.filter.ExcludeFilter;
import max.dirscan.scan.filter.FileExcludeFilter;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public final class DirScanner {

    private boolean isInit = false;
    private List<Path> dirForScan;
    private List<DirExcludeFilter> dirExcludeFilters = new LinkedList<>();
    private List<FileExcludeFilter> fileExcludeFilters = new LinkedList<>();

    private ForkJoinPool scanPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    public DirScanner() {
    }

    public void init(ParseResult result) {
        registerFilters(result.getFilters());
        this.dirForScan = result.getDirsToScan();
        isInit = true;
    }


    private void registerFilters(List<ExcludeFilter> filters) {
        filters.forEach(this::registerFilter);
    }

    private void registerFilter(ExcludeFilter filter) {
        if(filter instanceof DirExcludeFilter) {
            DirExcludeFilter dirExcludeFilter = (DirExcludeFilter) filter;
            dirExcludeFilters.add(dirExcludeFilter);
        } else if (filter instanceof FileExcludeFilter) {
            FileExcludeFilter fileExcludeFilter = (FileExcludeFilter)filter;
            fileExcludeFilters.add(fileExcludeFilter);
        } else {
            throw new IllegalArgumentException("[Dir Scanner] Error while registering filter. Filter has unknown type");
        }
    }

    public void startScan() {
        if(!isInit) {
            throw new InitException("Cannot start scanning: Dir Scanner is not initialized");
        }
        dirForScan.stream()
                .map(dir -> new DirScanning(dir, dirExcludeFilters, fileExcludeFilters))
                .forEach(scanPool::invoke);

        FilesProcessor.getProcessor().finish();
        while (scanPool.hasQueuedSubmissions() || scanPool.getActiveThreadCount() > 0) {
            Thread.yield();
        }
        scanPool.shutdown();
    }
}
