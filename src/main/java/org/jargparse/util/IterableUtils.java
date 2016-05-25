package org.jargparse.util;

import java.util.List;

public class IterableUtils {
    public static <T> String join(List<T> list, String separator) {
        StringBuilder builder = new StringBuilder(0);
        if (list.size() > 1)
            for (T item : list.subList(0, list.size() - 1))
                builder.append(item).append(separator);
        if (list.size() > 0)
            builder.append(list.get(list.size() - 1));
        return builder.toString();
    }
}
