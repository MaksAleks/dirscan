package max.dirscan.output;


import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;

public class FileReader implements Closeable {

    private Path file;

    private BufferedReader reader;

    public FileReader(Path file, Charset charset) {
        try {
            this.file = file;
            InputStream in = new FileInputStream(file.toFile());
            reader = new BufferedReader(new InputStreamReader(in, charset));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
