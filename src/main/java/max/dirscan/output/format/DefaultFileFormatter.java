package max.dirscan.output.format;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;

/**
 * Дефолтная реализация класса-форматтера
 */
public class DefaultFileFormatter extends FileFormatter {

    public DefaultFileFormatter(Path file, Charset charset) {
        super(file, charset);
    }

    /**
     * Метод получения форматированной строки с информацией о найденном файле
     * @param path - найденный файл
     * @param attrs - атрибуты файла
     * @return возвращается форматированная строка, которая будет записана в файл
     */
    @Override
    public final String formatEntry(Path path, BasicFileAttributes attrs)  {

        String name = path.toAbsolutePath().toString();

        FileTime date = attrs.creationTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String dateCreated = df.format(date.toMillis());
        Long size = attrs.size();

        return "[\n" +
                " name=" +
                name +
                "\n" +
                " date=" +
                dateCreated +
                "\n" +
                " size=" +
                size +
                "]";

    }
}
