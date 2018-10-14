package max.dirscan.output;

public interface EntryFormatter {

    public default String getEntryDelimiterPattern() {
        return "\n";
    }

    public String formatEntry(OutputEntry outputEntry);
}
