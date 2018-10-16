package max.dirscan.scan.filter;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public abstract class DirFilter implements ScanFilter {

    protected List<Path> dirToFilter;

    public DirFilter(List<Path> dirToFilter) {
        this.dirToFilter = dirToFilter;
    }

    public boolean isEmpty() {
        return dirToFilter.isEmpty();
    }

    @Override
    public abstract boolean filter(Path path);

    public static DirFilter emptyFilter() {
        return new DirFilter(Collections.emptyList()) {
            @Override
            public boolean filter(Path path) {
                return false;
            }
        };
    }
}