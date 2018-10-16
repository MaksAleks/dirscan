package max.dirscan;

import max.dirscan.config.DefaultOutputEntryWritingConfig;
import max.dirscan.input.InputParamsParser;
import max.dirscan.output.FilesProcessor;
import max.dirscan.scan.DirScanner;

public class Main {

    public static void main(String[] args) {

        String[] testArgs = {"/home/maxim/"};

        FilesProcessor.getProcessor().init(new DefaultOutputEntryWritingConfig());
        InputParamsParser paramsParser = new InputParamsParser();
        DirScanner scanner = new DirScanner(paramsParser.parse(testArgs));
        scanner.scan();
    }
}
