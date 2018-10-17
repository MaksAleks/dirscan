package max.dirscan.exceptions;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * класс ошибок возникающих на этапе парсинга входящих параметров
 */
public class ValidationParamsException extends RuntimeException {

    private String[] params;

    private String message;

    private final String PREFIX = "[Validation params ERROR]: ";

    public ValidationParamsException(String message, Throwable e, String[] params) {
        super(e);
        this.params  = params;
        this.message = PREFIX + paramsString() + message;
    }

    public ValidationParamsException(String message, String[] params) {
        this.params = params;
        this.message = PREFIX + paramsString() + message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String paramsString() {
        return Stream.of(params).collect(joining(" ", "Input params string: \"", "\"\n\t"));
    }
}
