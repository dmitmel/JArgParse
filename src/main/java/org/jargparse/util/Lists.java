package org.jargparse.util;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class Lists {
    public static <T> List<T> filter(List<T> list, org.jargparse.util.Predicate<T> predicate) {
        List<T> filtered = new ArrayList<>(list.size());
        for (T item : list)
            if (predicate.test(item))
                filtered.add(item);
        return filtered;
    }

    public static <IT, OT> List<OT> cast(List<IT> in) {
        List<OT> out = new ArrayList<>(0);
        for (IT item : in)
            out.add((OT) item);
        return out;
    }

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
