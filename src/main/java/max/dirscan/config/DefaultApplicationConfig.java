package max.dirscan.config;

import max.dirscan.input.DirExcluder;
import max.dirscan.input.DirsValidator;
import max.dirscan.input.Excluder;

import max.dirscan.input.InputParamsParser;
import max.dirscan.output.format.DefaultFileFormatter;
import max.dirscan.output.format.FileFormatter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;

public class DefaultApplicationConfig implements ApplicationConfig {

    @Override
    public Path outputFilePath()
    {
        return Paths.get("./result.txt").toAbsolutePath().normalize();
    }

    @Override
    public Charset outputFileCharset() {
        return StandardCharsets.UTF_8;
    }

    @Override
    public FileFormatter fileFormatter() {
        return new DefaultFileFormatter(outputFilePath(), outputFileCharset());
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