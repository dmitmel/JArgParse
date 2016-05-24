package org.jargparse.util;

import java.util.HashMap;
import java.util.Map;

public class Maps {
    public static <K, V> Map<K, V> set(Map<K, V> map, K key, V newValue) {
        if (map.containsKey(key)) {
            map.remove(key);
            map.put(key, newValue);
        } else
            map.put(key, newValue);

        return map;
    }

    public static <K, V> Map<K, V> filter(Map<K, V> map, Predicate<Pair<K, V>> predicate) {
        Map<K, V> outMap = new HashMap<>(map.size());

        for (K key : outMap.keySet()) {
            V value = outMap.get(key);
            if (predicate.test(new Pair<>(key, value)))
                outMap.put(key, value);
        }

        return outMap;
    }
}
