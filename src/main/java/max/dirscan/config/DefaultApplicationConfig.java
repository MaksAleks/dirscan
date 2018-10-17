package max.dirscan.config;

import max.dirscan.input.DirExcluder;
import max.dirscan.input.DirsValidator;
import max.dirscan.input.Excluder;
import max.dirscan.input.InputParamsParser;
import max.dirscan.output.format.DefaultFileFormatter;
import max.dirscan.output.format.FileFormatter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Дефолтная конфигурация приложения
 */
public class DefaultApplicationConfig implements ApplicationConfig {

    @Override
    public Path outputFilePath()
    {
        return Paths.get("./result.txt").toAbsolutePath().normalize();
    }

    @Override
    public Charset outputFileCharset() {
        return StandardCharsets.UTF_8;
    }

    /**
     * Возвращает дефлотный класс форматирования вывода в выходной файл
     * @return {@link DefaultFileFormatter}
     */
    @Override
    public FileFormatter fileFormatter() {
        return new DefaultFileFormatter(outputFilePath(), outputFileCharset());
    }

    /**
     * Реализует поведение по-умолчанию. Возвращает список дефолтных классов, которые
     * отвечают за исключение директорий из сканирования
     * @return список с одним элементом {@link DirExcluder}
     */
    @Override
    public List<Excluder> inputParamsExcluders() {
        return Collections.singletonList(new DirExcluder(new DirsValidator()));
    }

    /**
     * Отвечает основной класс отвечающий за парсинг входных параметров приложения
     *
     * @return {@link InputParamsParser}
     */
    @Override
    public InputParamsParser inputParamsParser() {
        return new InputParamsParser(new DirsValidator());
    }

}