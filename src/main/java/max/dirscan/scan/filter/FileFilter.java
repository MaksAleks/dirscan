package max.dirscan.scan.filter;

import java.nio.file.Path;

public abstract class FileFilter extends ScanFilter {

    @Override
    public abstract boolean filter(Path path);
}
