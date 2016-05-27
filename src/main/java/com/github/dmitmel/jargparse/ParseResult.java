package com.github.dmitmel.jargparse;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ParseResult implements Map<String, Object> {
    private Map<String, Object> underlying;

    public ParseResult(Map<String, Object> underlying) {
        this.underlying = underlying;
    }

    // Wrappers for underlying collection
    @Override public int size() { return underlying.size(); }
    @Override public boolean isEmpty() { return size() == 0; }
    @Override public boolean containsKey(Object key) { return underlying.containsKey(key); }
    @Override public boolean containsValue(Object value) { return underlying.containsValue(value); }
    @Override public Object get(Object key) { return underlying.get(key); }
    @Override public Object put(String key, Object value) { return underlying.put(key, value); }
    @Override public Object remove(Object key) { return underlying.remove(key); }
    @Override public void putAll(Map<? extends String, ?> m) { underlying.putAll(m); }
    @Override public void clear() { underlying.clear(); }
    @Override public Set<String> keySet() { return underlying.keySet(); }
    @Override public Collection<Object> values() { return underlying.values(); }
    @Override public Set<Map.Entry<String, Object>> entrySet() { return underlying.entrySet(); }

    public boolean getBoolean(String name) {
        return (boolean) get(name);
    }

    public String getString(String name) {
        return (String) get(name);
    }

    @SuppressWarnings("unchecked")
    public List<String> getList(String name) {
        return (List<String>) get(name);
    }

    /**
     * Returns {@code false} if {@link ArgumentParser#run(String...)} printed help or version, otherwise -
     * {@code true}.
     * @return {@code false} if {@link ArgumentParser#run(String...)} printed help or version, otherwise -
     * {@code true}.
     */
    public abstract boolean isRealResult();

    public static ParseResult realResult(Map<String, Object> map) {
        return new ParseResult(map) {
            @Override
            public boolean isRealResult() {
                return true;
            }
        };
    }

    public static ParseResult emptyResult() {
        // I give null with purpose, so you can't manipulate with empty result
        return new ParseResult(null) {
            @Override
            public boolean isRealResult() {
                return false;
            }

            @Override public int size() { return 0; }
        };
    }
}
