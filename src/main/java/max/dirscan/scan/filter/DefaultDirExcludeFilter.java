package max.dirscan.scan.filter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DefaultDirExcludeFilter extends DirExcludeFilter {

    public DefaultDirExcludeFilter(List<Path> dirsToFilter) {
        super(dirsToFilter);
    }

    @Override
    public boolean filter(Path path) {
        if (Files.isDirectory(path)) {
            return dirsToFilter.contains(path) ||
                    dirsToFilter.stream()
                            .anyMatch(parent -> isParent(path, parent));

        }
        return false;
    }

    private boolean isParent(Path mayBeChild, Path parent) {
        return mayBeChild.startsWith(parent);
    }
}
