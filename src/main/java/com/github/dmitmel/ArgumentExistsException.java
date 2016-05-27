package com.github.dmitmel;

/**
 * Exception can be thrown if you try to add new {@link com.github.dmitmel.argtypes.Argument} into {@link ArgumentList}.
 */
public class ArgumentExistsException extends RuntimeException {
    public ArgumentExistsException(String message) {
        super(message);
    }
}
