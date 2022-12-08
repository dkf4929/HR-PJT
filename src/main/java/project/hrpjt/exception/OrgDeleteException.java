package project.hrpjt.exception;

public class OrgDeleteException extends RuntimeException {

    public OrgDeleteException() {
    }

    public OrgDeleteException(String message) {
        super(message);
    }

    public OrgDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrgDeleteException(Throwable cause) {
        super(cause);
    }
}
