package com.infilos.auth;

import com.infilos.auth.core.AuthorityConfig;
import com.infilos.auth.core.TokenProfile;
import com.infilos.auth.intercept.context.WebContext;
import lombok.RequiredArgsConstructor;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.util.CommonHelper;

import java.time.Duration;

/**
 * <pre>
 * Main entry point, build your user profile after authentication, login and generate a token.
 * The generated token will the setup to configured token location. eg. header, cookie.
 * </pre>
 */
@RequiredArgsConstructor
public class Authentic {

    private final AuthorityConfig config;

    public String login(TokenProfile profile) {
        return login(profile, false, null);
    }

    public String login(TokenProfile profile, boolean isSkipAuthorizeUser) {
        return login(profile, isSkipAuthorizeUser, null);
    }

    /**
     * Generate token and setup cookie, if cookie enabled.
     *
     * @param profile             login user profile
     * @param isSkipAuthorizeUser mark user skip authorize
     * @param expireTime          token expiration
     * @return generated token
     */
    public String login(TokenProfile profile, boolean isSkipAuthorizeUser, String expireTime) {
        if (isSkipAuthorizeUser) {
            config.getAuthorityManager().markSkipAuthorize(profile);
        }

        String expiration = CommonHelper.isBlank(expireTime) ? config.getTokenExpiration() : expireTime;
        String token = config.getTokenGenerator().generate(profile, expiration);
        profile.setToken(token);

        if (config.enabledCookieToken()) {
            JEEContext jeeContext = WebContext.getJEEContext(config.isEnableSession());
            jeeContext.addResponseCookie(config.getCookieTokenLocator().getCookie(token, getCookieAge(expireTime)));
        }
        config.getProfileStateManager().active(profile);

        return token;
    }

    /**
     * Logout clean cookie or session if enabled.
     */
    public void logout(TokenProfile profile) {
        config.getLogoutHandler().handle(config, WebContext.getJEEContext(config.isEnableSession()), profile);
    }

    private int getCookieAge(String expireTime) {
        if (CommonHelper.isNotBlank(expireTime)) {
            return (int) Duration.parse(expireTime).minusSeconds(1).getSeconds();
        }
        return -1;
    }
}
