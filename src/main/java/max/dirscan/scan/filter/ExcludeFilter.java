package max.dirscan.scan.filter;

import java.nio.file.*;

/**
 * Класс отвечающий за фильтрацию файлов
 * Есть два абстрактных наследника:
 *  1) {@link DirExcludeFilter} - фильтр директорий
 *  2) {@link FileExcludeFilter} - фильтр файлов
 *
 *  Объекты данного типа формируются на основе парсинга входных параметров
 *  и поиска файлов для искоючения из сканирования. За их формирование отвечают
 *  объекты типа {@link max.dirscan.input.Excluder}.
 *
 *  Если объект типа {@link max.dirscan.input.Excluder} находит
 *  во входных параметрах файлы для исключения из сканирования, он формирует
 *  соответствующий фильтр
 */
public abstract class ExcludeFilter {

    /**
     * Основной метод фильтрации
     * @param path - файл для проверки на фильтрацию
     * @return true - если файл отфильтровался
     *         false - если файл не отфильтровался
     */
    public abstract boolean filter(Path path);

    /**
     * Метод, проверяющий, является ли фильтр пустым (т.е. ему нечего фильтровать)
     * @return true - пустой фильтр
     *         false - непустой фильтр
     */
    public abstract boolean isEmpty();

    /**
     * Статический метод для создания пустого фильтра
     * он ничего не фильтрует, поэтому метод {@link ExcludeFilter#filter(java.nio.file.Path)} всегда false
     * @return emptyFilter
     */
    public static ExcludeFilter emptyFilter() {

        return new ExcludeFilter() {
            @Override
            public boolean filter(Path path) {
                return false;
            }

            @Override
            public boolean isEmpty() {
                return true;
            }
        };
    }
}
