package max.dirscan.config;

import max.dirscan.output.EntryFormatter;

import java.nio.charset.Charset;

public interface OutputWriterConfig {

    String outputFilePath();

    EntryFormatter outputFileFormatter();

    Charset outputFileCharset();

    int bufferSize();
}
