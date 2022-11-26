package cat.uvic.teknos.m09.uf2.exceptions;

public class M09Uf2PracticeException extends RuntimeException {

    public M09Uf2PracticeException() {
    }

    public M09Uf2PracticeException(String message) {
        super(message);
    }

    public M09Uf2PracticeException(String message, Throwable cause) {
        super(message, cause);
    }

    public M09Uf2PracticeException(Throwable cause) {
        super(cause);
    }

    public M09Uf2PracticeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}