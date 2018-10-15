package max.dirscan.output;

import max.dirscan.config.OutputEntryProcessorConfig;

import java.nio.file.Path;

public abstract class OutputEntryWriter {

    private Path file;

    private OutputEntryProcessorConfig config;

    public OutputEntryWriter(Path file, OutputEntryProcessorConfig config) {
        this.file = file;
        this.config = config;
    }

    public OutputEntryProcessorConfig getConfig() {
        return config;
    }

    public abstract void writeEntry(OutputEntry entry);
}
