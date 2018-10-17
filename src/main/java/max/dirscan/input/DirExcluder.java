package max.dirscan.input;


import max.dirscan.exceptions.ValidationParamsException;
import max.dirscan.scan.filter.DefaultDirExcludeFilter;
import max.dirscan.scan.filter.ExcludeFilter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Дефолтная реализация класса {@link Excluder}
 * Данный класс служит для исключения из сканирования именно директорий
 */
public class DirExcluder extends Excluder {

    private final String KEY = "-";

    // Регулярное выражение на валидацию пути до директории в windows
    private final Pattern winPattern1
            = Pattern.compile("^([a-zA-Z]\\:\\\\|[a-zA-Z]\\:|\\\\)(\\\\[\\w\\.\\-\\_\\s]+|\\\\\\\\[\\w\\.\\-\\_\\s]+)*\\\\$");
    // Регулярное выражение на валидацию пути до директории в windows.
    // Отличается от первого тем, что разделителями директорий тут являются обратные слеши \
    private final Pattern winPattern2 = Pattern.compile("^([a-zA-Z]\\:)(\\/[\\w-_.\\s]+)+\\/$");
    // регулярное выражение на валидацию пути до директории в unix
    private final Pattern unixPattern = Pattern.compile("^\\/([\\w-_.\\s\\\\]+\\/)*$");

    private DirsValidator validator = new DirsValidator();

    public DirExcluder(DirsValidator validator) {
        this.validator = validator;
    }

    @Override
    protected String getKey() {
        return KEY;
    }

    @Override
    protected List<Pattern> excludePatterns() {
        List<Pattern> patterns = new LinkedList<>();
        patterns.add(winPattern1);
        patterns.add(winPattern2);
        patterns.add(unixPattern);
        return patterns;
    }

    /**
     * Метод создания фильтра
     * @param excludeFiles - список файлов для исключения из сканирования
     * @return В случае если список файлов для исключения пустой - возвращается пустой фильтр
     *         в противном случае возвращается дефолтный фильтр директорий {@link DefaultDirExcludeFilter}
     */
    @Override
    protected ExcludeFilter createFilter(List<Path> excludeFiles) {
        if (excludeFiles.isEmpty()) {
            return ExcludeFilter.emptyFilter();
        } else {
            return new DefaultDirExcludeFilter(excludeFiles, new DirsValidator());
        }
    }

    /**
     * Метод проверки на соответствие входного параметра регулярным выражениям
     * для данного Excluder'а
     * @param param - входящий параметр для валидации
     * @param params - список всех входящих параметров
     */
    @Override
    protected void validateAndAdd(String param, String[] params) {
        List<Matcher> matchers = excludePatterns().stream()
                .map(pattern -> pattern.matcher(""))
                .collect(Collectors.toList());
        boolean isDir = matchers.stream().anyMatch(matcher -> matcher.reset(param).matches());
        if(isDir) { // Если входящий параметр - валидный абсолютный путь до директории
            Path dir = Paths.get(param);
            if(!validator.isExists(dir)) {
                throw new ValidationParamsException("Directory \"" + param + "\" doesn't exist", params);
            }
            // И если эта директория сущеcтвует - добавляем её в список
            excludeFiles.add(dir);
        } else {
            throw new ValidationParamsException("Input param \"" + param + "\" has inappropriate format." +
                    " It should be an absolute Windows or Unix OS DIRECTORY path, i.e. it should end at \"\\\" or \"/\"\n" +
                    "Example 1: C:\\ProgramFiles\\\n" +
                    "Example 2: /home/user/", params);
        }
    }

}
