package max.dirscan.config;

import max.dirscan.output.format.FileFormatter;

import java.nio.charset.Charset;

import static max.dirscan.config.OutputEntryWritingConfig.Size.MByte;

public interface OutputEntryWritingConfig {

    String outputFilePath();

    Charset outputFileCharset();

    default int outputEntryBufferSize() {
        return 5*MByte;
    }

    FileFormatter entryFormatter();

    public class Size {

        public static final int Byte = 1;
        public static final int KByte = 1024;
        public static final int MByte = 1024*KByte;
        public static final int GByte = 1024*MByte;
    }
}
