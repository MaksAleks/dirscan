package max.dirscan;

import max.dirscan.config.DefaultOutputEntryWritingConfig;
import max.dirscan.input.DirExcluder;
import max.dirscan.input.InputParamsParser;
import max.dirscan.output.FilesProcessor;
import max.dirscan.scan.DirScanner;
import max.dirscan.scan.filter.DirFilter;

public class Main {

    public static void main(String[] args) {

        String[] testArgs = {
                "/home/maxim/Documents/",
                "/home/maxim/Загрузки/",
                "/home/maxim/etc/",
                "/home/maxim/usim/",
                "/home/maxim/.gradle/",
                "-",
                "/home/maxim/.gradle/"};

        FilesProcessor.getProcessor().init(new DefaultOutputEntryWritingConfig());
        InputParamsParser paramsParser = new InputParamsParser();
        DirScanner scanner = new DirScanner(paramsParser.parse(testArgs));
        DirExcluder excluder = new DirExcluder();
        DirFilter dirFilter = excluder.exclude(testArgs);
        scanner.registerFilter(dirFilter);
        scanner.scan();
    }
}
