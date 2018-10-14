package max.dirscan.output;


import max.dirscan.config.OutputWriterConfig;

import java.io.PrintWriter;
import java.nio.file.Path;

public class DefaultOutputEntryFileWriter extends OutputEntryFileWriter {

    private PrintWriter printWriter;

    public DefaultOutputEntryFileWriter(OutputWriterConfig config) {
        super(config);

    }

    @Override
    protected void flushEntry(OutputEntry entry, Path path) {

    }
}
