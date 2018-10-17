package max.dirscan.input;

import java.nio.file.*;

public class DirsValidator {

    public boolean isExist(Path path) {
        return Files.exists(path);
    }

    public boolean isNotExists(Path path) {
        return !Files.exists(path);
    }

    public boolean isDirectory(Path path) {
        return Files.isDirectory(path);
    }
}
