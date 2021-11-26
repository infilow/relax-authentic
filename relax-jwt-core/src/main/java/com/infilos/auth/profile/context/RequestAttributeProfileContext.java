package com.infilos.auth.profile.context;

import com.infilos.auth.profile.ProfileContext;
import com.infilos.auth.core.TokenProfile;
import org.pac4j.core.util.Pac4jConstants;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public final class RequestAttributeProfileContext implements ProfileContext {

    private static final String PROFILE_KEY = Pac4jConstants.USER_PROFILES;

    @Override
    public void clearProfile() {
        // do nothing
    }

    @Override
    public TokenProfile getProfile() {
        return (TokenProfile) request().getAttribute(PROFILE_KEY);
    }

    @Override
    public void setProfile(TokenProfile profile) {
        Assert.notNull(profile, "Only non-null TokenProfile instances are permitted");
        request().setAttribute(PROFILE_KEY, profile);
    }

    @SuppressWarnings("all")
    private HttpServletRequest request() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
}
