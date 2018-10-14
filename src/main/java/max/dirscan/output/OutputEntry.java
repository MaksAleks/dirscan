package max.dirscan.output;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public class OutputEntry implements Comparable<OutputEntry> {

    private final Path path;

    private final BasicFileAttributes attrs;

    public OutputEntry(Path path, BasicFileAttributes attrs) {
        this.path = path;
        this.attrs = attrs;
    }

    public Path getPath() {
        return path;
    }

    public BasicFileAttributes getAttrs() {
        return attrs;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int compareTo(OutputEntry o) {
        String name = path.toAbsolutePath().toString();
        String otherName = o.path.toAbsolutePath().toString();
        return name.compareTo(otherName);
    }

    public static class Builder {

        private Path path;
        private BasicFileAttributes attrs;

        public Builder path(Path path) {
            this.path = path;
            return this;
        }

        public Builder attrs(BasicFileAttributes attrs) {
            this.attrs = attrs;
            return this;
        }

        public OutputEntry build() {
            return new OutputEntry(path, attrs);
        }
    }

    @Override
    public boolean equals(Object otherObject) {

        if(this == otherObject) {
            return true;
        }

        if(otherObject == null) {
            return false;
        }

        if(getClass() != otherObject.getClass()) {
            return false;
        }

        OutputEntry other = (OutputEntry) otherObject;

        return path.equals(other.path)
                && attrs.equals(other.attrs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, attrs);
    }
}
