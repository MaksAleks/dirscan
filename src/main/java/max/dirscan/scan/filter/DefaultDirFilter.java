package max.dirscan.scan.filter;

import java.nio.file.Path;
import java.util.List;

public class DefaultDirFilter extends DirFilter {

    public DefaultDirFilter(List<Path> dirToFilter) {
        super(dirToFilter);
    }

    @Override
    public boolean filter(Path path) {
        return dirToFilter.contains(path);
    }
}
