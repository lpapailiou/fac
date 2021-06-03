package exceptions;

/**
 * This exception may be thrown during lexical validation.
 * It is used to wrap scanning errors, which would not be possible to recover. Like that,
 * invalid code can be rejected without leading the compiler to terminate.
 */
public class ScanException extends RuntimeException {

    /**
     * Throws an exception pointing out that an error occurred during the scanning process.
     *
     * @param message the exception message.
     */
    public ScanException(String message) {
        super(message);
    }

    /**
     * Throws an exception pointing out that an error occurred during the scanning process.
     *
     * @param message the exception message.
     * @param t       the error to wrap.
     */
    public ScanException(String message, Throwable t) {
        super(message, t);
    }

}
