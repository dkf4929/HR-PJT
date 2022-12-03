package project.hrpjt.exception;

//로그인 exception
public class NoSuchEmployeeException extends RuntimeException {
    public NoSuchEmployeeException() {
        super();
    }

    public NoSuchEmployeeException(String message) {
        super(message);
    }

    public NoSuchEmployeeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchEmployeeException(Throwable cause) {
        super(cause);
    }
}
