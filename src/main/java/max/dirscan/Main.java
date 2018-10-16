package max.dirscan;

import max.dirscan.input.Excluder;
import max.dirscan.input.InputParamsParser;
import max.dirscan.input.ParseResult;
import max.dirscan.scan.DirScanner;

import java.util.List;
import java.util.regex.Pattern;

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

//        FilesProcessor.getProcessor().init(new DefaultOutputEntryWritingConfig());
//        InputParamsParser paramsParser = new InputParamsParser();
//        DirScanner scanner = new DirScanner(paramsParser.parse(testArgs));
//        Excluder excluder = new DirExcluder();
//        ExcludeFilter filter = excluder.exclude(testArgs);
//        scanner.registerFilter(filter);
//        scanner.scan();


        String testRegex = args[0];
        Pattern pattern = Pattern.compile(testRegex);
        if(pattern.matcher("/home/maxim/.chache/").matches()) {

            System.out.println(testRegex + " matches " + "/home/maxim/.chache/");
        }
    }

}


class Application {

    private List<Excluder> excluders;

    private DirScanner scanner;

    public void init(String... inputParams) {

        InputParamsParser paramsParser = new InputParamsParser();
        ParseResult parseResult = paramsParser.parse(inputParams);

    }
}