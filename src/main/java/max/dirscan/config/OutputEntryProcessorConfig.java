package max.dirscan.config;

import max.dirscan.output.EntryFormatter;
import max.dirscan.output.OutputEntryWriter;

import java.nio.charset.Charset;

public interface OutputEntryProcessorConfig {

    String outputFilePath();

    EntryFormatter outputEntryFormatter();

    Charset outputFileCharset();

    int bufferSize();

    Class<? extends OutputEntryWriter> getWriterClass();
}
