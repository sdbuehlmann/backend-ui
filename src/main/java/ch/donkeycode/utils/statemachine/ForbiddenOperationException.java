package ch.donkeycode.utils.statemachine;

public class ForbiddenOperationException extends Exception {
    public ForbiddenOperationException(String message) {
        super("Forbidden Operation: " + message);
    }
}
