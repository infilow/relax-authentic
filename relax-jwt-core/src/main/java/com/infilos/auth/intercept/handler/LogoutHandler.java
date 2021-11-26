package com.infilos.auth.intercept.handler;

import com.infilos.auth.core.ProfileHolder;
import com.infilos.auth.core.AuthorityConfig;
import com.infilos.auth.core.TokenProfile;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.JEEContext;

/**
 * Process request context and profile after logout.
 */
@FunctionalInterface
public interface LogoutHandler {

    void handle(AuthorityConfig config, JEEContext context, TokenProfile profile);

    @Slf4j
    class Default implements LogoutHandler {

        @Override
        @SuppressWarnings("unchecked")
        public void handle(AuthorityConfig config, JEEContext context, TokenProfile profile) {
            if (config.enabledCookieToken()) {
                context.addResponseCookie(config.getCookieTokenLocator().clean());
                log.debug("logoutHandler clean cookie success!");
            }
            if (config.isEnableSession()) {
                boolean b = context.getSessionStore().destroySession(context);
                if (b) {
                    log.debug("LogoutHandler destroySession success!");
                } else {
                    log.warn("LogoutHandler destroySession fail!");
                }
            }
            config.getProfileStateManager().discard(profile);
            ProfileHolder.clearProfile();
        }
    }
}
