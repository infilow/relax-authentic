package com.infilos.abac.api;

public interface PolicyVerifier {

    /**
     * Verify action on specific resource, with defined policy rules.
     */
    void verify(Object resource, String action);

    /**
     * Enum action support.
     */
    <T extends Enum<T>> void verify(Object resource, T action);
}
