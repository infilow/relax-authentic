package com.infilos.auth.intercept.matching;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;

/**
 * Implement pac4j matcher, only compare request path, if matches, apply filter.
 */
public class PathOnlyMatcher implements Matcher {
    private final String expectPath;

    public PathOnlyMatcher(String expectPath) {
        CommonHelper.assertNotBlank("expectPath", expectPath);
        this.expectPath = expectPath;
    }

    @Override
    public boolean matches(WebContext context) {
        return expectPath.equals(context.getPath());
    }
}

