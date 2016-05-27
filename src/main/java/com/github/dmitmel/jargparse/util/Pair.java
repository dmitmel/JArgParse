package com.github.dmitmel.jargparse.util;

import java.util.HashMap;
import java.util.Map;

public class Pair<K, V> {
    public K key;
    public V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public Pair() {
        this(null, null);
    }

    public Pair<K, V> set(K value1, V value2) {
        this.key = value1;
        this.value = value2;
        return this;
    }

    public Map<K, V> asOneElementMap() {
        Map<K, V> map = new HashMap<>(1);
        map.put(key, value);
        return map;
    }
}
