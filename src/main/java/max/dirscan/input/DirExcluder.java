package max.dirscan.input;


import max.dirscan.scan.filter.DefaultDirFilter;
import max.dirscan.scan.filter.DirFilter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirExcluder extends Excluder {

    private final String KEY = "-";

    private final Pattern winPattern1 = Pattern.compile("^([a-zA-Z]\\:\\\\|\\\\)(\\\\[\\w.\\-_\\s\\\\]+)\\\\$");
    private final Pattern winPattern2 = Pattern.compile("^([a-zA-Z]\\:\\/)(.+)\\/$");
    private final Pattern unixPattern = Pattern.compile("^(\\/)(.+)\\/$");
    private List<Matcher> matchers = new ArrayList<>(3);

    {
        matchers.add(winPattern1.matcher(""));
        matchers.add(winPattern2.matcher(""));
        matchers.add(unixPattern.matcher(""));
    }

    private List<Path> dirsToExclude = new ArrayList<>();

    @Override
    protected String getKey() {
        return KEY;
    }

    public DirFilter exclude(String... params) {
        List<String> listParams = Arrays.asList(params);
        if(!listParams.contains(KEY)) {
            return DirFilter.emptyFilter();
        }
        listParams = listParams.subList(listParams.indexOf(KEY)+1, listParams.size());
        for (String param : listParams) {
            boolean isDir = matchers.stream().anyMatch(matcher -> matcher.reset(param).matches());
            if (isDir) {
                dirsToExclude.add(Paths.get(param));
            } else {
                return new DefaultDirFilter(dirsToExclude);
            }
        }
        if(dirsToExclude.isEmpty()) {
            return DirFilter.emptyFilter();
        }
        return new DefaultDirFilter(dirsToExclude);
    }
}
