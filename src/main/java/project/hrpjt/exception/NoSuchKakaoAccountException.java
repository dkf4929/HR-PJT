package project.hrpjt.exception;

public class NoSuchKakaoAccountException extends RuntimeException {
    public NoSuchKakaoAccountException() {
    }

    public NoSuchKakaoAccountException(String message) {
        super(message);
    }

    public NoSuchKakaoAccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchKakaoAccountException(Throwable cause) {
        super(cause);
    }
}
