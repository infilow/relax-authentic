package com.infilos.auth.intercept.flow;

import com.infilos.auth.core.ProfileHolder;
import com.infilos.auth.core.AuthorityConfig;
import com.infilos.auth.error.RedirectHttpAction;
import com.infilos.auth.intercept.BaseFilter;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.matching.matcher.Matcher;

@Slf4j
public class LogoutFilter extends BaseFilter {
    
    public LogoutFilter(Matcher pathMatcher) {
        super(pathMatcher);
    }

    @Override
    protected HttpAction process(AuthorityConfig config, JEEContext context) {
        if (log.isDebugEnabled()) {
            log.debug("access logout");
        }
        config.getLogoutHandler().handle(config, context, ProfileHolder.getProfile());
        
        return RedirectHttpAction.INSTANCE;
    }

    @Override
    public int order() {
        return 300;
    }
}
