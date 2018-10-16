package max.dirscan.input;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputParamsParser {

    private final Pattern winPattern1 = Pattern.compile("^([a-zA-Z]\\:\\\\|\\\\)(\\\\[\\w.\\-_\\s\\\\]+)\\\\$");
    private final Pattern winPattern2 = Pattern.compile("^([a-zA-Z]\\:\\/)(.+)\\/$");
    private final Pattern unixPattern = Pattern.compile("^(\\/)(.+)\\/$");
    private List<Matcher> matchers = new ArrayList<>(3);
    {
        matchers.add(winPattern1.matcher(""));
        matchers.add(winPattern2.matcher(""));
        matchers.add(unixPattern.matcher(""));
    }

    private List<String> dirsToScan = new ArrayList<>();

    public List<String> parse(String... params) {
        for(String param : params) {
            boolean isDir = matchers.stream().anyMatch(matcher -> matcher.reset(param).matches());
            if(isDir) {
                dirsToScan.add(param);
            } else {
                return dirsToScan;
            }
        }
        return dirsToScan;
    }

}
