package project.hrpjt.exception;

import org.springframework.security.core.AuthenticationException;

public class NoAuthorityException extends AuthenticationException {
    public NoAuthorityException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public NoAuthorityException(String msg) {
        super(msg);
    }
}
