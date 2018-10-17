package max.dirscan.scan.filter;

import java.nio.file.*;

public abstract class ExcludeFilter {

    public abstract boolean filter(Path path);

    public abstract boolean isEmpty();

    public final boolean isNotEmpty() {
        return !isEmpty();
    }

    public static ExcludeFilter emptyFilter() {

        return new ExcludeFilter() {
            @Override
            public boolean filter(Path path) {
                return false;
            }

            @Override
            public boolean isEmpty() {
                return true;
            }
        };
    }
}
