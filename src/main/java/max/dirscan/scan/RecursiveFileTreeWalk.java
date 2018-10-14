package max.dirscan.scan;

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

    public RecursiveFileTreeWalk(Path dir) {
        this.dir = dir;
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
                    System.out.println(file + "\t" + Thread.currentThread());
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
