package max.dirscan.exceptions;

public class ValidationParamsException extends RuntimeException {

    private String[] params;

    public ValidationParamsException(String message, Throwable e, String... params) {
        super(message, e);
        this.params  = params;
    }

    public ValidationParamsException(String message, String... params) {
        super(message);
        this.params = params;
    }

    public ValidationParamsException(String message, Throwable e) {
        super(message,e);
    }

    public ValidationParamsException(String message) {
        super(message);
    }
}
