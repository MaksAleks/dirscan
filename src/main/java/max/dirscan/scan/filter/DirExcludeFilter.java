package max.dirscan.scan.filter;

import max.dirscan.input.DirsValidator;

import java.nio.file.Path;
import java.util.List;

public abstract class DirExcludeFilter extends ExcludeFilter {

    private List<Path> dirsToFilter;

    private DirsValidator validator;

    public DirExcludeFilter(List<Path> dirsToFilter, DirsValidator validator) {
        this.dirsToFilter = dirsToFilter;
        this.validator = validator;
    }

    public List<Path> getDirsToFilter() {
        return dirsToFilter;
    }

    public DirsValidator getValidator() {
        return validator;
    }

    @Override
    public boolean isEmpty() {
        return dirsToFilter.isEmpty();
    }

    protected abstract boolean filterDir(Path path);

    @Override
    public final boolean filter(Path path) {
        if(validator.isDirectory(path)) {
            return filterDir(path);
        }
        return false;
    }
}