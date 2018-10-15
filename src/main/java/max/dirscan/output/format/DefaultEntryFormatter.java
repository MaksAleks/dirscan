package max.dirscan.output.format;

import max.dirscan.output.OutputEntry;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class DefaultEntryFormatter implements EntryFormatter {

    @Override
    public String getEntryDelimiterPattern() {
        return "(?<=\\])(?=\\[)";
    }

    @Override
    public String formatEntry(OutputEntry outputEntry) {

        Path path = outputEntry.getPath();
        BasicFileAttributes attrs = outputEntry.getAttrs();
        String name = path.toAbsolutePath().toString();
        String date = attrs.creationTime().toString();
        Long size = attrs.size();

        return "[\n" +
                " name=" +
                name +
                "\n" +
                " date=" +
                date +
                "\n" +
                " size=" +
                size +
                "]";
    }
}
