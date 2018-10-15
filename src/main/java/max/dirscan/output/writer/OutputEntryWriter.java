package max.dirscan.output.writer;

import max.dirscan.config.OutputEntryWritingConfig;
import max.dirscan.output.OutputEntry;

import java.nio.file.Path;

public abstract class OutputEntryWriter {

    private Path file;

    private OutputEntryWritingConfig config;

    public OutputEntryWriter(Path file, OutputEntryWritingConfig config) {
        this.file = file;
        this.config = config;
    }

    public OutputEntryWritingConfig getConfig() {
        return config;
    }

    public abstract void writeEntry(OutputEntry entry);
}
