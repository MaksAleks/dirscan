package max.dirscan.output.format;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import java.text.SimpleDateFormat;

public class DefaultFileFormatter extends FileFormatter {

    public DefaultFileFormatter(Path file, Charset charset) {
        super(file, charset);
    }

    @Override
    public final String formatEntry(Path path, BasicFileAttributes attrs) throws IOException {

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
