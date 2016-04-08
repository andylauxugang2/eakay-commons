package cn.eakay.commons.dao.persistence.exception;

/**
 * Created by xugang on 16/4/7.
 */
public class DAOException extends RuntimeException {
    private static final long serialVersionUID = 393757215938489730L;

    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
