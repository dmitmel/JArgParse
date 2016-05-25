package org.jargparse;

/**
 * Exception can be thrown if you try to add new {@link org.jargparse.argtypes.Argument} into {@link ArgumentList}.
 */
public class ArgumentExistsException extends RuntimeException {
    public ArgumentExistsException(String message) {
        super(message);
    }
}
