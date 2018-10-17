package max.dirscan.exceptions;

/**
 *  Класс ошибок происходящих на этапе инициализации приложения
 */
public class InitException extends RuntimeException {

    public InitException(String message, Throwable e) {
        super(message,e);
    }

    public InitException(String message) {
        super(message);
    }
}
