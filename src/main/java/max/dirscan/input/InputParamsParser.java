package max.dirscan.input;


import max.dirscan.exceptions.ValidationParamsException;
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

    private final Pattern winPattern1 = Pattern.compile("^([a-zA-Z]\\:\\\\|[a-zA-Z]\\:|\\\\)(\\\\[\\w\\.\\-\\_\\s]+)+\\\\$");
    private final Pattern winPattern2 = Pattern.compile("^([a-zA-Z]\\:)(\\/[\\w-_.\\s]+)+\\/$");
    private final Pattern unixPattern = Pattern.compile("^\\/([\\w-_.\\s\\\\]+\\/)*$");
    private List<Matcher> matchers = new ArrayList<>(3);
    {
        matchers.add(winPattern1.matcher(""));
        matchers.add(winPattern2.matcher(""));
        matchers.add(unixPattern.matcher(""));
    }

    private DirsValidator validator;

    List<Path> dirsToScan = new LinkedList<>();
    List<Excluder> excluders = new LinkedList<>();

    public InputParamsParser(DirsValidator validator) {
        this.validator = validator;
    }

    public InputParamsParser(List<Excluder> excluders) {
        this.excluders.addAll(excluders);
    }

    public void registerExcluders(List<Excluder> excluders) {
        this.excluders.addAll(excluders);
    }

    public void registerExcluder(Excluder excluder) {
        excluders.add(excluder);
    }

    public ParseResult parse(String... params) {

        List<String> exludersKeys = excluders.stream()
                .map(Excluder::getKey)
                .collect(Collectors.toList());

        for(String param : params) {
            if(exludersKeys.contains(param)) {
                break;
            }
            boolean isDir = matchers.stream().anyMatch(matcher -> matcher.reset(param).matches());
            if(isDir) {
                Path dir = Paths.get(param);
                if(validator.isNotExists(dir)) {
                    throw new ValidationParamsException("Directory \"" + dir.toString() + "\" doesn't exist", params);
                }
                dirsToScan.add(dir);
            } else {
                throw new ValidationParamsException("Input param \"" + param + "\" has inappropriate format." +
                        " It should be an absolute Windows or Unix OS DIRECTORY path, i.e. it should end at \"\\\" or \"/\"\n" +
                        "Example 1: C:\\ProgramFiles\\\n" +
                        "Example 2: /home/user/", params);
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
        excludeFilters = excludeFilters.stream()
                .filter(f -> !f.isEmpty())
                .collect(Collectors.toList());

        return new ParseResult(dirsToScan, excludeFilters);
    }

}
