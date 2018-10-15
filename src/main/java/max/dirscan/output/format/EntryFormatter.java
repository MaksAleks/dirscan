package max.dirscan.output.format;

import max.dirscan.output.OutputEntry;

public interface EntryFormatter {

    public default String getEntryDelimiterPattern() {
        return "\n";
    }

    public String formatEntry(OutputEntry outputEntry);
}
