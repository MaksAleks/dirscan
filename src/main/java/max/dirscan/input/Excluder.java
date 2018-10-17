package max.dirscan.input;


import max.dirscan.scan.filter.ExcludeFilter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.nio.file.*;

/**
 * Абстрактный класс отвечающий за исключение файлов из сканирования
 * Дефолтная реализация, которая используется в приложении - {@link DirExcluder}
 */
public abstract class Excluder {

    /**
     * Список фалов, которые нужно исключить из сканирования
     */
    protected List<Path> excludeFiles = new LinkedList<>();

    /**
     * Возвращает ключ после которого идут файлы для исключения из сканирования,
     * обрабатываемые данным Excluder'ом
     * @return Возвращает строку. Желательно возвращать строку начинающуся с '-',
     *         например -key
     */
    protected abstract String getKey();

    /**
     * Возвращает список паттернов-регулярных выражений, на основе которых будут из входных параметров
     * будут выбираться валидные для данного Excluder'a файлы для исключения из сканирования
     * @return Список объектов java.util.regex.Pattern
     */
    protected abstract List<Pattern> excludePatterns();

    /**
     * Метод создания фильтра, который будет использоваться во время сканирования
     * для фильтрации файлов, которые были найдены во входных параметрах
     * @param excludeFiles - список файлов для исключения из сканирования
     * @return возвращается объект типа {@link ExcludeFilter}
     */
    protected abstract ExcludeFilter createFilter(List<Path> excludeFiles);

    /**
     * Метод для валидации каждого входящего параметра.
     * Валидация происходит на основе регулярных выражений из метода {@link Excluder#excludePatterns()}
     * Реализация логики валидации остается за пользователем.
     * Дефолтная реализация, используемая в приложении - {@link max.dirscan.input.DirExcluder#validateAndAdd(java.lang.String, java.lang.String[])}
     * @param param - входящий параметр для валидации
     * @param params - список всех входящих параметров
     */
    protected abstract void validateAndAdd(String param, String... params);


    /**
     * Здесь заключена общая логика проверки входящих параметров для дальнейшей их обработки
     * Метод возвращает объект фильтра, соответствующего данному Excluder'у, который отвечает
     * за фильтрацию файлов в процессе сканирования
     * @param params - спиок всех входящих параметров
     * @return возвращается объект типа {@link ExcludeFilter}
     */
    public final ExcludeFilter exclude(String... params) {
        List<String> listParams = Arrays.asList(params);

        /**
         * Если лист параметров не содержит ключа для данного Excluder'а
         * тогда вызываем метод создания фильтра на пустом списке.
         * В данном случае создается т.н. пустой фильтр {@link ExcludeFilter#emptyFilter()}
         */
        if (!listParams.contains(getKey())) {
            return createFilter(excludeFiles);
        }

        /**
         * Получаем список всех параметров начиная со следующего после ключа
         */
        int keyIndex = listParams.indexOf(getKey());
        listParams = listParams.subList(keyIndex + 1, listParams.size());
        for (String param : listParams) {

            if (param.startsWith("-")) {
                // Если параметр начинается на "-", значит это следующий ключ
                // и значит все параметры для этого Excluder'а были обработаны
                break;
            }
            /**
             * Валидируем параметр и в случае успеха добавляем его в список файлов для исключения
             */
            validateAndAdd(param, params);
        }
        return createFilter(excludeFiles);
    }

}
