package max.dirscan.input;


import max.dirscan.exceptions.ValidationParamsException;
import max.dirscan.scan.filter.DefaultDirExcludeFilter;
import max.dirscan.scan.filter.ExcludeFilter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DirExcluder extends Excluder {

    private final String KEY = "-";

    private final Pattern WIN_PATH = Pattern.compile("^([a-zA-Z]\\:\\\\|\\\\)(\\\\[\\w\\.\\-\\_\\s]+)+\\\\$");
    private final Pattern WIN_PATH_2 = Pattern.compile("^([a-zA-Z]\\:)(\\/[\\w-_.\\s]+)+\\/$");
    private final Pattern UNIX_PATH = Pattern.compile("^\\/([\\w-_.\\s\\\\]+\\/)*$");

    private DirsValidator validator = new DirsValidator();

    public DirExcluder(DirsValidator validator) {
        this.validator = validator;
    }

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
    protected ExcludeFilter createFilter(List<Path> excludeFiles) {
        if (excludeFiles.isEmpty()) {
            return ExcludeFilter.emptyFilter();
        } else {
            return new DefaultDirExcludeFilter(excludeFiles, new DirsValidator());
        }
    }

    @Override
    protected void validateAndAdd(String param, String[] params) {
        List<Matcher> matchers = excludePatterns().stream()
                .map(pattern -> pattern.matcher(""))
                .collect(Collectors.toList());
        boolean isDir = matchers.stream().anyMatch(matcher -> matcher.reset(param).matches());
        if(isDir) {
            Path dir = Paths.get(param);
            if(!validator.isExists(dir)) {
                throw new ValidationParamsException("Directory \"" + param + "\" doesn't exist", params);
            }
            excludeFiles.add(dir);
        } else {
            throw new ValidationParamsException("Input param \"" + param + "\" has inappropriate format." +
                    " It should be an absolute Windows or Unix OS DIRECTORY path, i.e. it should end at \"\\\" or \"/\"\n" +
                    "Example 1: C:\\ProgramFiles\\\n" +
                    "Example 2: /home/user/", params);
        }
    }

}
