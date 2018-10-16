package max.dirscan.scan.filter;

import java.nio.file.*;

public abstract class ScanFilter {

    public abstract boolean filter(Path path);

    public abstract boolean isEmpty();

    public static ScanFilter emptyFilter() {

        return new ScanFilter() {
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
