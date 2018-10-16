package max.dirscan.scan;

import max.dirscan.exceptions.InitException;
import max.dirscan.output.FilesProcessor;
import max.dirscan.scan.filter.DirExcludeFilter;
import max.dirscan.scan.filter.FileExcludeFilter;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class DirScanning extends RecursiveAction {

    private final Path dir;

    private FilesProcessor processor;

    private List<DirExcludeFilter> dirExcludeFilters;

    private List<FileExcludeFilter> fileExcludeFilters;

    public DirScanning(Path dir, List<DirExcludeFilter> dirExcludeFilters, List<FileExcludeFilter> fileExcludeFilters) {
        this.dir = dir;
        this.dirExcludeFilters = dirExcludeFilters;
        this.fileExcludeFilters = fileExcludeFilters;
        processor = FilesProcessor.getProcessor();
        if(!processor.isStared()) {
            throw new InitException("Cannot start scanning: File Processor hasn't been started yet");
        }
    }

    @Override
    protected void compute() {
        final List<DirScanning> scanningList = new ArrayList<>();
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    boolean skip = dirExcludeFilters.stream().anyMatch(f -> f.filter(dir));
                    if(skip) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    if (!dir.equals(DirScanning.this.dir)) {
                        DirScanning w = new DirScanning(dir, dirExcludeFilters, fileExcludeFilters);
                        w.fork();
                        scanningList.add(w);
                        return FileVisitResult.SKIP_SUBTREE;
                    } else {
                        return FileVisitResult.CONTINUE;
                    }
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    boolean process = fileExcludeFilters.stream().noneMatch(f -> f.filter(file));
                    if(process) {
                        processor.process(file.toAbsolutePath().toString());
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (DirScanning w : scanningList) {
            w.join();
        }
    }
}
