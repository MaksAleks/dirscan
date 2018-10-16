package max.dirscan.input;


import max.dirscan.scan.filter.DefaultDirExcludeFilter;
import max.dirscan.scan.filter.ExcludeFilter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DirExcluder extends Excluder {

    private final String KEY = "-";

    private final Pattern WIN_PATH = Pattern.compile("^([a-zA-Z]\\:\\\\|\\\\)(\\\\[\\w\\.\\-\\_\\s]+)+\\\\$");
    private final Pattern WIN_PATH_2 = Pattern.compile("^([a-zA-Z]\\:)(\\/[\\w-_.\\s]+)+\\/$");
    private final Pattern UNIX_PATH = Pattern.compile("^\\/([\\w-_.\\s\\\\]+\\/)*$");

    @Override
    protected String getKey() {
        return KEY;
    }

    @Override
    protected List<Pattern> excludePatterns() {
        List<Pattern> patterns = new LinkedList<>();
        patterns.add(WIN_PATH);
        patterns.add(WIN_PATH_2);
        patterns.add(UNIX_PATH);
        return patterns;
    }

    @Override
    protected ExcludeFilter createFilter(List<String> excludeFiles) {
        if (excludeFiles.isEmpty()) {
            return ExcludeFilter.emptyFilter();
        } else {
            List<Path> dirsToFilter = excludeFiles.stream()
                    .map(Paths::get)
                    .collect(Collectors.toList());
            return new DefaultDirExcludeFilter(dirsToFilter);
        }
    }

}
