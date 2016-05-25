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

}
