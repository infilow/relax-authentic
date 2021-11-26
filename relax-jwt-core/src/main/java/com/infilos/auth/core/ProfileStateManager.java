package com.infilos.auth.core;

/**
 * Stateful user profile manager, can be persisted.
 */
public interface ProfileStateManager {
    
    /**
     * Mark profile actively after login.
     */
    default void active(TokenProfile profile){
    }

    /**
     * Mark profile inactively after logout or kick out.
     */
    default void discard(TokenProfile profile){
    }
    
    /**
     * Check if an user profile is active.
     */
    default boolean isOnline(TokenProfile profile) {
        return true;
    }
    
    ProfileStateManager NOOP = new ProfileStateManager() {
    };
}
