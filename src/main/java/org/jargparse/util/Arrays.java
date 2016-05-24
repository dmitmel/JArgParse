package org.jargparse.util;

import java.util.Iterator;

public class Arrays {
    @SafeVarargs
    public static <E> Iterator<E> iteratorFromArray(final E... items) {
        return new Iterator<E>() {
            int currentPos = 0;

            @Override
            public boolean hasNext() {
                return currentPos < items.length;
            }

            @Override
            public E next() {
                E item = items[currentPos];
                currentPos++;
                return item;
            }

            @Override
            public void remove() {

            }
        };
    }
}
