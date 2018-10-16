package max.dirscan.scan.filter;

import java.nio.file.*;

public interface ScanFilter {

    public boolean filter(Path path);
}
