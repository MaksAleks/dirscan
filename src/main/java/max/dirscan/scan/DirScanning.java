package max.dirscan.scan;

import max.dirscan.output.FilesProcessor;

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

    public DirScanning(Path dir) {
        this.dir = dir;
        processor = FilesProcessor.getProcessor();
        if(!processor.isInit()) {
            throw new RuntimeException("FilesProcessor is not initialized");
        }
    }

    @Override
    protected void compute() {
        final List<DirScanning> scanningList = new ArrayList<>();
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!dir.equals(DirScanning.this.dir)) {
                        DirScanning w = new DirScanning(dir);
                        w.fork();
                        scanningList.add(w);

                        return FileVisitResult.SKIP_SUBTREE;
                    } else {
                        return FileVisitResult.CONTINUE;
                    }
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    processor.process(file.toAbsolutePath().toString());
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
