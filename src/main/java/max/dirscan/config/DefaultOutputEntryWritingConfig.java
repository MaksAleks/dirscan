package max.dirscan.config;

import max.dirscan.output.format.DefaultFileFormatter;
import max.dirscan.output.format.FileFormatter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class DefaultOutputEntryWritingConfig implements OutputEntryWritingConfig {

    @Override
    public String outputFilePath() {
        return "./output.txt";
    }

    @Override
    public Charset outputFileCharset() {
        return StandardCharsets.UTF_8;
    }

    @Override
    public FileFormatter entryFormatter() {
        return new DefaultFileFormatter(Paths.get(outputFilePath()), outputFileCharset());
    }
}