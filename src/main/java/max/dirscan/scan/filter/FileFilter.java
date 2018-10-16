package max.dirscan.scan.filter;

import java.nio.file.Path;

public abstract class FileFilter implements ScanFilter {

    @Override
    public abstract boolean filter(Path path);
}
