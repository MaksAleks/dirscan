package max.dirscan.output;


import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class FileWriter implements Closeable {

    private PrintWriter writer;

    public FileWriter(Path file, Charset charset) {
        try {
            BufferedWriter bw = Files.newBufferedWriter(file, charset, CREATE, APPEND);
            writer = new PrintWriter(bw, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeln(String line) {
        writer.println(line);
    }

    public void write(String line) {
        writer.print(line);
    }

    public void writeLines(Collection<String> lines) {
        lines.forEach(this::write);
    }

    @Override
    public void close() {
        writer.close();
    }

    public void writelnLines(List<String> lines) {
        lines.forEach(this::writeln);
    }
}
