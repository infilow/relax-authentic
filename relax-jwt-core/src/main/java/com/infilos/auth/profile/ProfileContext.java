package com.infilos.auth.profile;

import com.infilos.auth.core.TokenProfile;

public interface ProfileContext {
    /**
     * Clear the current TokenProfile.
     */
    void clearProfile();

    /**
     * Obtain the current TokenProfile.
     */
    TokenProfile getProfile();

    /**
     * Set the current TokenProfile.
     */
    void setProfile(TokenProfile profile);   
}
