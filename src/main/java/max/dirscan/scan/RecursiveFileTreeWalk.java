package max.dirscan.scan;

import max.dirscan.output.OutputEntryProcessor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class RecursiveFileTreeWalk extends RecursiveAction {

    private final Path dir;

    private OutputEntryProcessor processor;

    public RecursiveFileTreeWalk(Path dir) {
        this.dir = dir;
        processor = OutputEntryProcessor.getProcessor();
        if(!processor.isInit()) {
            throw new RuntimeException("OutputEntryProcessor is not initialized");
        }
    }

    @Override
    protected void compute() {
        final List<RecursiveFileTreeWalk> walks = new ArrayList<>();
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!dir.equals(RecursiveFileTreeWalk.this.dir)) {
                        RecursiveFileTreeWalk w = new RecursiveFileTreeWalk(dir);
                        w.fork();
                        walks.add(w);

                        return FileVisitResult.SKIP_SUBTREE;
                    } else {
                        return FileVisitResult.CONTINUE;
                    }
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    processor.process(file, attrs);
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

        for (RecursiveFileTreeWalk w : walks) {
            w.join();
        }
    }
}
