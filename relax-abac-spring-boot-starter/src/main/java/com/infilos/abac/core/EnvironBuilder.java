package com.infilos.abac.core;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EnvironBuilder {
    private final Map<String, Object> environ = new HashMap<String, Object>() {{
        put("time", System.currentTimeMillis());
        put("datetime", LocalDateTime.now());
    }};

    public EnvironBuilder add(String key, Object value) {
        environ.put(key, value);
        return this;
    }

    public EnvironBuilder addAll(Map<String, Object> map) {
        environ.putAll(map);
        return this;
    }

    public Map<String, Object> build() {
        return environ;
    }

    public static EnvironBuilder create() {
        return new EnvironBuilder();
    }
}
