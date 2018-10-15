package max.dirscan.config;

import max.dirscan.output.format.DefaultEntryFormatter;
import max.dirscan.output.writer.DefaultOutputEntryWriter;
import max.dirscan.output.format.EntryFormatter;
import max.dirscan.output.writer.OutputEntryWriter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DefaultOutputEntryWritingConfig implements OutputEntryWritingConfig {

    @Override
    public String outputFilePath() {
        return "./output.txt";
    }

    @Override
    public EntryFormatter outputEntryFormatter() {
        return new DefaultEntryFormatter();
    }

    @Override
    public Charset outputFileCharset() {
        return StandardCharsets.UTF_8;
    }

    @Override
    public Class<? extends OutputEntryWriter> outputEntryWriterClass() {
        return DefaultOutputEntryWriter.class;
    }
}