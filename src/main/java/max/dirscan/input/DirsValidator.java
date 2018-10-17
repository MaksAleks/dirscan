package max.dirscan.input;

import java.nio.file.*;

public class DirsValidator {

    public boolean isExists(Path path) {
        return Files.exists(path);
    }

    public boolean isNotExists(Path path) {
        return !isExists(path);
    }

    public boolean isDirectory(Path path) {
        return Files.isDirectory(path);
    }

    public boolean isNotDirectory(Path path) {
        return !isDirectory(path);
    }
}
