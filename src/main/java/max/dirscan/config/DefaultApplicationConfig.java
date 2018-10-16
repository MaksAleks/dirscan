package max.dirscan.config;

import max.dirscan.input.DirExcluder;
import max.dirscan.input.Excluder;
import max.dirscan.input.InputParamsLengthValidator;
import max.dirscan.input.InputParamsValidator;
import max.dirscan.output.format.DefaultFileFormatter;
import max.dirscan.output.format.FileFormatter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class DefaultApplicationConfig implements ApplicationConfig {

    @Override
    public String outputFilePath() {
        return "./output.txt";
    }

    @Override
    public Charset outputFileCharset() {
        return StandardCharsets.UTF_8;
    }

    @Override
    public FileFormatter fileFormatter() {
        return new DefaultFileFormatter(Paths.get(outputFilePath()), outputFileCharset());
    }

    @Override
    public List<Excluder> inputParamsExcluders() {
        return Collections.singletonList(new DirExcluder());
    }

    @Override
    public List<InputParamsValidator> inputParamsValidators() {
        return Collections.singletonList(new InputParamsLengthValidator());
    }
}