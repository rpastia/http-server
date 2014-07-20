package ro.pastia.server.protocol.http.exception;

/**
 * Exception thrown by members of HttpRequest when different parts of the request can't be parsed.
 */
public class InvalidRequestException extends Exception {

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
