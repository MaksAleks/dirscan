package max.dirscan.scan.filter;

import java.nio.file.Path;

/**
 * Абстрактный класс для фильтрации файлов
 * На текущей момент нет реализации
 */
public abstract class FileExcludeFilter extends ExcludeFilter {

    @Override
    public abstract boolean filter(Path path);
}
