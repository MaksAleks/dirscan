package max.dirscan.input;


import max.dirscan.scan.filter.ExcludeFilter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.nio.file.*;

public abstract class Excluder {

    protected List<Path> excludeFiles = new LinkedList<>();

    protected abstract String getKey();

    protected abstract List<Pattern> excludePatterns();

    protected abstract ExcludeFilter createFilter(List<Path> excludeFiles);

    protected abstract void validateAndAdd(String param, String... params);


    public final ExcludeFilter exclude(String... params) {
        List<String> listParams = Arrays.asList(params);

        if (!listParams.contains(getKey())) {
            return createFilter(excludeFiles);
        }

        int keyIndex = listParams.indexOf(getKey());
        listParams = listParams.subList(keyIndex + 1, listParams.size());
        for (String param : listParams) {

            if (param.startsWith("-")) {
                // Если параметр начинается на "-", значит это следующий ключ
                // и значит все параметры для этого Excluder'а были обработаны
                break;
            }
            validateAndAdd(param, params);
        }
        return createFilter(excludeFiles);
    }

}
