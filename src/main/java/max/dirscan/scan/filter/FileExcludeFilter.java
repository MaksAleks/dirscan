package max.dirscan.scan.filter;

import java.nio.file.Path;

public abstract class FileExcludeFilter extends ExcludeFilter {

    @Override
    public abstract boolean filter(Path path);
}
