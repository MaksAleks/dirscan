package max.dirscan.scan.filter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DefaultDirFilter extends DirFilter {

    public DefaultDirFilter(List<Path> dirToFilter) {
        super(dirToFilter);
    }

    @Override
    public boolean filter(Path path) {
        if (Files.isDirectory(path)) {
            return dirToFilter.contains(path) ||
                    dirToFilter.stream()
                            .anyMatch(parent -> isParent(path, parent));

        }
        return false;
    }

    private boolean isParent(Path mayBeChild, Path parent) {
        return mayBeChild.startsWith(parent);
    }
}
