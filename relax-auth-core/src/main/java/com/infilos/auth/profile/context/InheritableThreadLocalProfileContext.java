package com.infilos.auth.profile.context;

import com.infilos.auth.profile.ProfileContext;
import com.infilos.auth.core.TokenProfile;
import org.springframework.util.Assert;

public final class InheritableThreadLocalProfileContext implements ProfileContext {

    private static final ThreadLocal<TokenProfile> contextHolder = new InheritableThreadLocal<>();

    @Override
    public void clearProfile() {
        contextHolder.remove();
    }

    @Override
    public TokenProfile getProfile() {
        return contextHolder.get();
    }

    @Override
    public void setProfile(TokenProfile profile) {
        Assert.notNull(profile, "Only non-null TokenProfile instances are permitted");
        contextHolder.set(profile);
    }
}
