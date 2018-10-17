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

/**
 * Класс отвечающий за инициализацию и старта процесса сканирования
 * Он создает задачи {@link DirScanning}, сканирующие директории по приципу Fork Join
 */
public final class DirScanner {

    private boolean isInit = false;
    // Директории для сканирования
    private List<Path> dirForScan;
    // Фильтры директорий
    private List<DirExcludeFilter> dirExcludeFilters = new LinkedList<>();
    // Фильтры файлов
    private List<FileExcludeFilter> fileExcludeFilters = new LinkedList<>();

    // Fork Join pool с потоками для сканирования директорий
    private ForkJoinPool scanPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    private DirScanner() {
    }

    private static DirScanner scanner = new DirScanner();

    public static DirScanner getScanner() {
        return scanner;
    }

    /**
     * Метод инициализации сканера
     * @param result - результат парсинга входящих параметров
     */
    public void init(ParseResult result) {
        registerFilters(result.getFilters());
        this.dirForScan = result.getDirsToScan();
        isInit = true;
    }


    private void registerFilters(List<ExcludeFilter> filters) {
        filters.forEach(this::registerFilter);
    }

    private void registerFilter(ExcludeFilter filter) {
        if(filter.isEmpty()) return;
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

    /**
     * Метод сканирования
     */
    public void scan() {
        if(!isInit) {
            throw new InitException("Cannot start scanning: Dir Scanner is not initialized");
        }
        // Для каждой директории создается таска DirScanning и помещается в ForkJoinPool,
        // в котором она начинает исполняться
        dirForScan.stream()
                .map(dir -> new DirScanning(dir, dirExcludeFilters, fileExcludeFilters))
                .forEach(scanPool::invoke);

        // После завершения сканирования вызывается метод,
        // сигнализирующий FilesProcessor'у о том, что сканирование завершено
        FilesProcessor.getProcessor().finish();
    }
}
