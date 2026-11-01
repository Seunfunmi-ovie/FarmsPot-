package ng.farmsPot.exceptions;

/** Thrown when a signed-in user tries to touch something that belongs to someone else. */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
