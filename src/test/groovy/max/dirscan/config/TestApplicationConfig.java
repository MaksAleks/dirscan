package max.dirscan.config;

import max.dirscan.input.DirExcluder;
import max.dirscan.input.DirsValidator;
import max.dirscan.input.Excluder;
import max.dirscan.input.InputParamsParser;
import max.dirscan.output.format.FileFormatter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;

public class TestApplicationConfig implements ApplicationConfig {
    @Override
    public String outputFilePath() {
        return "./test/output.txt";
    }

    @Override
    public Charset outputFileCharset() {
        return StandardCharsets.UTF_8;
    }

    @Override
    public FileFormatter fileFormatter() {
        return new FileFormatter(Paths.get(outputFilePath()), outputFileCharset()) {

            @Override
            public String formatEntry(Path path, BasicFileAttributes attrs) throws IOException {
                return path.toFile().getAbsolutePath();
            }
        };
    }

    @Override
    public List<Excluder> inputParamsExcluders() {
        return Collections.singletonList(new DirExcluder(new DirsValidator()));
    }

    @Override
    public InputParamsParser inputParamsParser() {
        return new InputParamsParser(new DirsValidator());
    }
}
