package exceptions;

public class ScanException extends RuntimeException {

    public ScanException(String message) {
        super(message);
    }

    public ScanException(String message, Throwable err) {
        super(message, err);
    }

}
