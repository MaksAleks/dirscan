package max.dirscan.input;


import max.dirscan.scan.filter.ExcludeFilter;

import java.util.List;
import java.util.Objects;
import java.nio.file.*;

public class ParseResult {

    private final List<Path> dirsToScan;

    private final List<ExcludeFilter> filters;

    public ParseResult(List<Path> dirsToScan, List<ExcludeFilter> filters) {
        this.dirsToScan = dirsToScan;
        this.filters = filters;
    }

    public List<ExcludeFilter> getFilters() {
        return filters;
    }

    public List<Path> getDirsToScan() {
        return dirsToScan;
    }

    @Override
    public boolean equals(Object otherObject) {
        if(this == otherObject) {
            return true;
        }
        if(otherObject == null) {
            return false;
        }
        if(this.getClass() != otherObject.getClass()) {
            return false;
        }

        ParseResult other = (ParseResult) otherObject;

        return dirsToScan.equals(other.dirsToScan)
                && filters.equals(other.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dirsToScan, filters);
    }
}
