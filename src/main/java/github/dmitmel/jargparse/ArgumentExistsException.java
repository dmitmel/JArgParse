package github.dmitmel.jargparse;

/**
 * Exception can be thrown if you try to add new {@link Argument} into {@link ArgumentList}.
 */
public class ArgumentExistsException extends RuntimeException {
    public ArgumentExistsException(String message) {
        super(message);
    }
}
