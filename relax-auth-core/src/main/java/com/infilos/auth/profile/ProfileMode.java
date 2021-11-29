package com.infilos.auth.profile;

import java.util.Arrays;

public enum ProfileMode {
    REQUEST, THREADLOCAL, INHERIT_THREADLOCAL;
    
    public static ProfileMode of(String modeName) {
        return Arrays.stream(ProfileMode.values())
            .filter(m -> m.name().equalsIgnoreCase(modeName))
            .findFirst()
            .orElse(REQUEST);
    }
}
