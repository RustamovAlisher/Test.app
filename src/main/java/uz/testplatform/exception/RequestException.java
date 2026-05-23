package uz.testplatform.exception;


public class RequestException extends RuntimeException {
    public RequestException(String message) {
        super(message);
    }
}