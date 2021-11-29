package com.infilos.auth.intercept;

import com.infilos.auth.core.AuthorityConfig;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;

public abstract class BaseFilter implements AuthFilter {

    private final Matcher pathMatcher;
    
    public BaseFilter(Matcher pathMatcher) {
        CommonHelper.assertNotNull("pathMatcher", pathMatcher);
        this.pathMatcher = pathMatcher;
    }
    
    @Override
    public HttpAction filter(AuthorityConfig config, JEEContext context) {
        if (pathMatcher.matches(context)) {
            try {
                return process(config, context);
            } catch (Exception ex) {
                return handleException(ex);
            }
        }

        return null;
    }

    /**
     * Process the context when match, with return a http action.
     */
    protected abstract HttpAction process(AuthorityConfig config, JEEContext context);

    protected HttpAction handleException(Exception e) {
        if (e instanceof HttpAction) {
            return (HttpAction) e;
        }
        
        throw new RuntimeException(e);
    }
}
