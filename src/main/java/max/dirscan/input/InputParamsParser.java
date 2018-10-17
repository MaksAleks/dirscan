package max.dirscan.input;


import max.dirscan.exceptions.ValidationParamsException;
import max.dirscan.scan.filter.ExcludeFilter;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * Класс отвечающий за поиск во входящих параметров директорий для сканирования
 * и файлов для исключения из сканирования с помощью классов Excluder'ов
 */
public class InputParamsParser {

    // Регулярное выражение на валидацию пути до директории в windows
    private final Pattern winPattern1
            = Pattern.compile("^([a-zA-Z]\\:\\\\|[a-zA-Z]\\:|\\\\)(\\\\[\\w\\.\\-\\_\\s]+|\\\\\\\\[\\w\\.\\-\\_\\s]+)*\\\\$");
    // Регулярное выражение на валидацию пути до директории в windows.
    // Отличается от первого тем, что разделителями директорий тут являются обратные слеши \
    private final Pattern winPattern2 = Pattern.compile("^([a-zA-Z]\\:)(\\/[\\w-_.\\s]+)+\\/$");
    // регулярное выражение на валидацию пути до директории в unix
    private final Pattern unixPattern = Pattern.compile("^\\/([\\w-_.\\s\\\\]+\\/)*$");
    private List<Matcher> matchers = new ArrayList<>(3);
    {
        matchers.add(winPattern1.matcher(""));
        matchers.add(winPattern2.matcher(""));
        matchers.add(unixPattern.matcher(""));
    }

    private DirsValidator validator;

    /**
     * Cписок директорий, которые нужно будет просканировать
     */
    List<Path> dirsToScan = new LinkedList<>();

    /**
     * Список зарегистрированных классов Excluder'ов
     */
    List<Excluder> excluders = new LinkedList<>();

    public InputParamsParser(DirsValidator validator) {
        this.validator = validator;
    }

    public InputParamsParser(List<Excluder> excluders) {
        this.excluders.addAll(excluders);
    }

    public void registerExcluders(List<Excluder> excluders) {
        this.excluders.addAll(excluders);
    }

    public void registerExcluder(Excluder excluder) {
        excluders.add(excluder);
    }

    /**
     * Логика парсинга входных параметров
     * Метод возвращает объект {@link ParseResult}, который содержит результаты парсинга:
     *  - список директорий для сканирования
     *  - список фильтров
     * @param params - входные параметры приложения
     * @return возвращается объект типа {@link ParseResult}
     */
    public ParseResult parse(String... params) {

        // Получаем список ключей всех зарегистрированных классов Excluder'ов
        List<String> exludersKeys = excluders.stream()
                .map(Excluder::getKey)
                .collect(Collectors.toList());

        // цикл поиска директорий для сканирования
        for(String param : params) {
            if(exludersKeys.contains(param)) {
                // Если входной параметр - ключ, то следующий за ним
                // параметр должен обрабатываться одним из Excluder'ов
                // поэтому прерываем дальнейший поиск директорий для сканирования
                break;
            }
            boolean isDir = matchers.stream().anyMatch(matcher -> matcher.reset(param).matches());
            if(isDir) {
                // Если входящий параметр - валидный абсолютный путь до директории
                Path dir = Paths.get(param);
                if(!validator.isExists(dir)) {
                    throw new ValidationParamsException("Directory \"" + dir.toString() + "\" doesn't exist", params);
                }
                // И данная директория существует - добавляем в список для сканирования
                dirsToScan.add(dir);
            } else {
                throw new ValidationParamsException("Input param \"" + param + "\" has inappropriate format." +
                        " It should be an absolute Windows or Unix OS DIRECTORY path, i.e. it should end at \"\\\" or \"/\"\n" +
                        "Example 1: C:\\ProgramFiles\\\n" +
                        "Example 2: /home/user/", params);
            }
        }

        // Цикл поиска файлов для исключения и создание фильтров
        List<ExcludeFilter> excludeFilters = excluders.stream()
                .map(excluder -> excluder.exclude(params))
                .collect(Collectors.toList());

        // Если среди найденых директорий для сканирования
        // есть те, которые присутствуют в списке для фильтрации,
        // мы эти директории сразу убираем
        ListIterator<Path> iterator = dirsToScan.listIterator();
        while (iterator.hasNext()) {

            Path path = iterator.next();
            boolean exclude = excludeFilters.stream()
                    .anyMatch(f -> f.filter(path));
            if(exclude) {
                iterator.remove();
            }
        }
        // Если среди фильтров оказались пустые,
        // то фильтровать им нечего - убираем их из списка
        excludeFilters = excludeFilters.stream()
                .filter(f -> !f.isEmpty())
                .collect(Collectors.toList());

        // Записываем и возвраем результат
        return new ParseResult(dirsToScan, excludeFilters);
    }

}
