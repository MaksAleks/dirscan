package max.dirscan.exceptions;

public class InitException extends RuntimeException {

    public InitException(String message, Throwable e) {
        super(message,e);
    }

    public InitException(String message) {
        super(message);
    }
}
