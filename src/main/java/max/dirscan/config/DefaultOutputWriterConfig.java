package max.dirscan.config;

import max.dirscan.output.DefaultEntryFormatter;
import max.dirscan.output.EntryFormatter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DefaultOutputWriterConfig implements OutputWriterConfig {

    private final String OUTPUT_FILE_DEFAULT_PATH = "./output.txt";

    private final EntryFormatter formatter = new DefaultEntryFormatter();

    @Override
    public String outputFilePath() {
        return OUTPUT_FILE_DEFAULT_PATH;
    }

    @Override
    public EntryFormatter outputFileFormatter() {
        return formatter;
    }

    @Override
    public Charset outputFileCharset() {
        return StandardCharsets.UTF_8;
    }

    @Override
    public int bufferSize() {
        return 500000; //500 KByte
    }
}
