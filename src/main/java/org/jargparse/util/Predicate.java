package org.jargparse.util;

/**
 * Tests values with specified condition. Class from Java 1.8 for Java 1.7.
 */
public interface Predicate<T> {
    boolean test(T t);
}
