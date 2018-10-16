package max.dirscan.output.format;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class DefaultFileFormatter extends FileFormatter {

    public DefaultFileFormatter(Path file, Charset charset) {
        super(file, charset);
    }

    @Override
    public String formatEntry(Path path) throws IOException {

        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            String name = path.toAbsolutePath().toString();
            String date = attrs.creationTime().toString();
            Long size = attrs.size();

            return "[\n" +
                    " name=" +
                    name +
                    "\n" +
                    " date=" +
                    date +
                    "\n" +
                    " size=" +
                    size +
                    "]";
        } catch (NoSuchFileException e) {
            return "";
        }
    }
}
