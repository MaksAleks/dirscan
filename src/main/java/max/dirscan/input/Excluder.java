package max.dirscan.input;

import max.dirscan.scan.filter.ExcludeFilter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class Excluder {

    protected abstract String getKey();

    protected abstract List<Pattern> excludePatterns();

    protected abstract ExcludeFilter createFilter(List<String> excludeFiles);

    public final ExcludeFilter exclude(String... params) {
        List<String> listParams = Arrays.asList(params);
        List<String> excludeFiles = new LinkedList<>();

        if(!listParams.contains(getKey())) {
            return createFilter(excludeFiles);
        }
        List<Matcher> excludeMatchers = excludePatterns().stream()
                .map(pattern -> pattern.matcher(""))
                .collect(Collectors.toList());

        int keyIndex = listParams.indexOf(getKey());
        listParams = listParams.subList(keyIndex + 1, listParams.size());
        for(String param: listParams) {
            boolean isMatches = excludeMatchers.stream()
                    .anyMatch(matcher -> matcher.reset(param).matches());
            if(isMatches) {
                excludeFiles.add(param);
            } else {
                break;
            }
        }
        return createFilter(excludeFiles);
    }
}
