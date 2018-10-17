package max.dirscan.output.format;

import max.dirscan.output.FileReader;
import max.dirscan.output.FileWriter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public abstract class FileFormatter {

    private Path file;
    private Charset charset;

    public FileFormatter(Path file, Charset charset) {
        this.file = file;
        this.charset = charset;
    }

    public abstract String formatEntry(Path path, BasicFileAttributes attrs) throws IOException;

    public void  format(Path sortedFile) {
        String formattedFilePath = file.toFile().getAbsolutePath() + "_formatted";
        Path formattedFile = Paths.get(formattedFilePath);
        try (
                FileReader reader = new FileReader(sortedFile, charset);
                FileWriter writer = new FileWriter(formattedFile, charset)
        ) {
            String filePath;
            while ((filePath = reader.readLine()) != null) {
                Path path = Paths.get(filePath);
                if (Files.exists(path)) {
                    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                    String formattedEntry = formatEntry(path, attrs);
                    writer.write(formattedEntry);
                }
            }
            Files.move(formattedFile, file);
            sortedFile.toFile().deleteOnExit();
        } catch (NoSuchFileException e) {
            throw new RuntimeException(String.format("File %s not Found!", file.toFile().getAbsolutePath()), e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
