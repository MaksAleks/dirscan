package max.dirscan.config;

import max.dirscan.input.Excluder;
import max.dirscan.input.InputParamsParser;
import max.dirscan.output.format.FileFormatter;
import max.dirscan.sort.ExternalMergeSort;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

import static max.dirscan.config.ApplicationConfig.Size.MByte;

/**
 * Интерфейс, отвечающий за конфигурацию приложения.
 * Это основная точка для расширения приложения
 */
public interface ApplicationConfig {

    /**
     * @return путь до выходного файла приложения.
     */
    Path outputFilePath();


    /**
     * @return возвращает кодировку выходного файла приложения
     */
    Charset outputFileCharset();

    /**
     * @return Возвращает класс, отвечающий форматирование вывода в выходной файл
     */
    FileFormatter fileFormatter();

    /**
     * @return Возвращает список классов, отвечающих за исключение файлов из сканирования
     */
    List<Excluder> inputParamsExcluders();

    /**
     * @return Возвращает класс, отвечающий за парсинг входных параметров приложения
     */
    InputParamsParser inputParamsParser();

    /**
     * Т.к. выходной файл может быть достаточно большим, чтобы не поместиться в ОЗУ,
     * нужен какой-то буфер {@link max.dirscan.output.SortedFilesBuffer} и временные файлы, с помощью которых производится
     * сортировка {@link ExternalMergeSort} выходного файла
     * @return возвращает максимальный размер буфера, Значение по умолчанию 2 мегабайта
     */
    default int outputEntryBufferSize() {
        return 2*MByte;
    }

    public class Size {

        public static final int Byte = 1;
        public static final int KByte = 1024;
        public static final int MByte = 1024*KByte;
        public static final int GByte = 1024*MByte;
    }
}
