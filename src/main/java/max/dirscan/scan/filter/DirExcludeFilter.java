package max.dirscan.scan.filter;

import max.dirscan.input.DirsValidator;

import java.nio.file.Path;
import java.util.List;

/**
 * Класс для фильтрации директорий.
 * Имеет одного реализованного наследника {@link DefaultDirExcludeFilter}
 */
public abstract class DirExcludeFilter extends ExcludeFilter {

    // директории для фильтрации
    protected List<Path> dirsToFilter;

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

    /**
     * Метод для фильтрации директорий
     * @param path - файл для проверки на фильтрацию
     * @return true - если отфильтровал
     *         false - если не отфильтровал
     */
    @Override
    public final boolean filter(Path path) {
        // Так как этот фильтр для директорий, то
        // любой файл, который не является директорией
        // отфильтрован не будет
        if(validator.isDirectory(path)) {
            return filterDir(path);
        }
        return false;
    }
}