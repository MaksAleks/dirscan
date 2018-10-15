package max.dirscan.input;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputParamsParser {

    private List<String> dirsToScan = new ArrayList<>();

    private Pattern dirsToScanPattern = Pattern.compile("^(\\/)?([^\\/\0]+(\\/)?)+$");

    public List<String> parse(String... args) {
        for(String arg : args) {
            Matcher matcher = dirsToScanPattern.matcher(arg);
            if(matcher.matches()) {
                dirsToScan.add(arg);
            } else {
                return dirsToScan;
            }
        }
        return dirsToScan;
    }

}
