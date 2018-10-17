package max.dirscan.scan.filter;

import java.nio.file.Path;
import java.util.List;

public abstract class DirExcludeFilter extends ExcludeFilter {

    protected List<Path> dirsToFilter;

    public DirExcludeFilter(List<Path> dirsToFilter) {
        this.dirsToFilter = dirsToFilter;
    }

    public boolean isEmpty() {
        return dirsToFilter.isEmpty();
    }

    @Override
    public abstract boolean filter(Path path);
}