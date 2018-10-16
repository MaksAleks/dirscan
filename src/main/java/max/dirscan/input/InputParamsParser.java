package max.dirscan.input;


import max.dirscan.scan.filter.ExcludeFilter;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InputParamsParser {

    private final Pattern winPattern1 = Pattern.compile("^([a-zA-Z]\\:\\\\|\\\\)(\\\\[\\w\\.\\-\\_\\s]+)+\\\\$");
    private final Pattern winPattern2 = Pattern.compile("^([a-zA-Z]\\:)(\\/[\\w-_.\\s]+)+\\/$");
    private final Pattern unixPattern = Pattern.compile("^\\/([\\w-_.\\s\\\\]+\\/)*$");
    private List<Matcher> matchers = new ArrayList<>(3);
    {
        matchers.add(winPattern1.matcher(""));
        matchers.add(winPattern2.matcher(""));
        matchers.add(unixPattern.matcher(""));
    }

    List<Path> dirsToScan = new LinkedList<>();
    List<Excluder> excluders = new LinkedList<>();

    public void registerExcluder(Excluder excluder) {
        excluders.add(excluder);
    }

    public ParseResult parse(String... params) {
        for(String param : params) {
            boolean isDir = matchers.stream().anyMatch(matcher -> matcher.reset(param).matches());
            if(isDir) {
                dirsToScan.add(Paths.get(param));
            } else {
                break;
            }
        }
        List<ExcludeFilter> excludeFilters = excluders.stream()
                .map(excluder -> excluder.exclude(params))
                .collect(Collectors.toList());

        ListIterator<Path> iterator = dirsToScan.listIterator();
        while (iterator.hasNext()) {

            Path path = iterator.next();
            boolean exclude = excludeFilters.stream()
                    .anyMatch(f -> f.filter(path));
            if(exclude) {
                iterator.remove();
            }
        }

        return new ParseResult(dirsToScan, excludeFilters);
    }

}
