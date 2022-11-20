package marketit.backend.project.order.exception;

public class OutOfQuantityException extends RuntimeException{
    public OutOfQuantityException() {
    }

    public OutOfQuantityException(String message) {
        super(message);
    }

    public OutOfQuantityException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutOfQuantityException(Throwable cause) {
        super(cause);
    }

    public OutOfQuantityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
