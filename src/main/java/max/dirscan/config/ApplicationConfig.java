package max.dirscan.config;

import max.dirscan.input.Excluder;
import max.dirscan.output.format.FileFormatter;

import java.nio.charset.Charset;
import java.util.List;

import static max.dirscan.config.ApplicationConfig.Size.MByte;

public interface ApplicationConfig {

    String outputFilePath();

    Charset outputFileCharset();

    FileFormatter fileFormatter();

    List<Excluder> inputParamsExcluders();

    default int outputEntryBufferSize() {
        return 5*MByte;
    }

    public class Size {

        public static final int Byte = 1;
        public static final int KByte = 1024;
        public static final int MByte = 1024*KByte;
        public static final int GByte = 1024*MByte;
    }
}
