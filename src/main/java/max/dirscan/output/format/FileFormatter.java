package max.dirscan.output.format;

import max.dirscan.output.FileReader;
import max.dirscan.output.FileWriter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Абстрактный класс, отвечающий за форматирование выходного файла
 * Дефолтная реализация, которая используется в приложении - {@link DefaultFileFormatter}
 */
public abstract class FileFormatter {

    private Path file;
    private Charset charset;

    public FileFormatter(Path file, Charset charset) {
        this.file = file;
        this.charset = charset;
    }

    /**
     * Метод для форматирования записи с информацией о найденом файле
     *
     * @param path - найденный файл
     * @param attrs - атрибуты файла
     * @return Возвращает отформатированную строку, которая будет записано в выходной файл
     */
    public abstract String formatEntry(Path path, BasicFileAttributes attrs);

    /**
     * Общая логика форматирования файла
     * Результатом является выходной файл, отформатированный и отсортированный в алфавитном порядке
     * по абслютном путям до файлов
     *
     * @param sortedFile - отсортированный по алфавиту файл
     *                   с аболютными путями до файлов, найденных
     *                   во время сканирования
     */
    public void  format(Path sortedFile) {
        try (
                FileReader reader = new FileReader(sortedFile, charset);
                FileWriter writer = new FileWriter(file, charset)
        ) {
            String filePath;
            // Основной цилк форматирования отсортированного файла
            while ((filePath = reader.readLine()) != null) {
                // Читаем строчки из отсортированного файла, пока там что-то есть
                // Каждая строчка - абсолютный путь до найденного файла
                Path path = Paths.get(filePath);
                if (Files.exists(path)) {// Если данный файл на момент записи существует в системе
                    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                    // мы формируемт форматированную строчку с информацией о данном файле
                    String formattedEntry = formatEntry(path, attrs);
                    // и пишем её в выходной файл
                    writer.write(formattedEntry);
                }
            }
            sortedFile.toFile().deleteOnExit();
        } catch (NoSuchFileException e) {
            throw new RuntimeException(String.format("File %s not Found!", file.toFile().getAbsolutePath()), e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
