package max.dirscan;

import max.dirscan.config.DefaultOutputEntryProcessorConfig;
import max.dirscan.input.InputParamsParser;
import max.dirscan.output.OutputEntryProcessor;
import max.dirscan.scan.DirScanner;

public class Main {

    public static void main(String[] args) {

        String[] testArgs = {"/home/maxim/forScan"};

        OutputEntryProcessor.getProcessor().init(new DefaultOutputEntryProcessorConfig());
        InputParamsParser paramsParser = new InputParamsParser();
        DirScanner scanner = new DirScanner(paramsParser.parse(testArgs));
        scanner.scan();
    }
}
