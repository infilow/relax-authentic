package com.infilos.auth.intercept.flow;

import com.infilos.auth.core.ProfileHolder;
import com.infilos.auth.core.AuthorityConfig;
import com.infilos.auth.core.TokenProfile;
import com.infilos.auth.intercept.BaseFilter;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.*;
import org.pac4j.core.matching.matcher.Matcher;

/**
 * Main filter for authorize.
 */
@Slf4j
public class AuthorizeFilter extends BaseFilter {

    public AuthorizeFilter(Matcher pathMatcher) {
        super(pathMatcher);
    }

    @Override
    protected HttpAction process(AuthorityConfig config, JEEContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Access secured path: {} {}", context.getRequestMethod(), context.getPath());
        }
        if (!config.checkMatching(context)) {
            return BadRequestAction.INSTANCE;
        }
        
        TokenProfile profile = config.getTokenProfileExtractor().extract(context);
        
        if (profile == null) {
            return UnauthorizedAction.INSTANCE;
        }
        if (!config.getProfileStateManager().isOnline(profile) || !config.checkAuthorization(context, profile)) {
            return ForbiddenAction.INSTANCE;
        }
        
        ProfileHolder.setProfile(profile);
        if (log.isDebugEnabled()) {
            log.debug("Authenticate and authorize succed");
        }
        
        return null;
    }

    @Override
    public int order() {
        return 200;
    }
}
