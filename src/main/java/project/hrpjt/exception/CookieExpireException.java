package project.hrpjt.exception;

public class CookieExpireException extends RuntimeException {
    public CookieExpireException() {
        super();
    }

    public CookieExpireException(String message) {
        super(message);
    }

    public CookieExpireException(String message, Throwable cause) {
        super(message, cause);
    }

    public CookieExpireException(Throwable cause) {
        super(cause);
    }
}
