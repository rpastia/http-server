package ro.pastia.server.protocol.http.exception;

/**
 * Created by Radu on 16.07.2014.
 */
public class InvalidRequestException extends Exception {

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
