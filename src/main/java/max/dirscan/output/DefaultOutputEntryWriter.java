package max.dirscan.output;

import max.dirscan.config.OutputEntryProcessorConfig;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Path;

public class DefaultOutputEntryWriter extends OutputEntryWriter {

    private PrintWriter writer;

    public DefaultOutputEntryWriter(Path file, OutputEntryProcessorConfig config) {
        super(file, config);
        try {
            FileOutputStream fstream = new FileOutputStream(file.toAbsolutePath().toString());
            OutputStreamWriter streamWriter = new OutputStreamWriter(fstream, config.outputFileCharset());
            writer = new PrintWriter(streamWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeEntry(OutputEntry entry) {
        String formattedEntry = getConfig().outputEntryFormatter().formatEntry(entry);
        writer.print(formattedEntry);
    }
}
