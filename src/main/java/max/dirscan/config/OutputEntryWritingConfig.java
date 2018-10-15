package max.dirscan.config;

import max.dirscan.output.format.EntryFormatter;
import max.dirscan.output.writer.OutputEntryWriter;

import java.nio.charset.Charset;

public interface OutputEntryWritingConfig {

    String outputFilePath();

    EntryFormatter outputEntryFormatter();

    Charset outputFileCharset();

    default int outputEntryBufferSize() {
        return 500000;
    }

    Class<? extends OutputEntryWriter> outputEntryWriterClass();
}
