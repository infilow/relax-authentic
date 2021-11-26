package com.infilos.auth.intercept;

import com.infilos.auth.core.ProfileHolder;
import com.infilos.auth.core.AuthorityConfig;
import com.infilos.auth.intercept.context.WebContext;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.HttpAction;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Construct an authorize filter chain.
 */
public class AuthFilters {
    private final List<AuthFilter> filters = new ArrayList<>();

    public void addFilter(AuthFilter filter) {
        filters.add(filter);
    }

    public List<AuthFilter> getFilters() {
        return filters.stream()
            .peek(AuthFilter::initCheck)
            .sorted(Comparator.comparingInt(AuthFilter::order))
            .collect(Collectors.toList());
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, AuthorityConfig config) {
        final JEEContext context = WebContext.getJEEContext(request, response, config.isEnableSession());

        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }
        
        for (AuthFilter interceptor : getFilters()) {
            try {
                HttpAction action = interceptor.filter(config, context);
                if (action != null) {
                    config.getHttpErrorHandler().handle(config, context, action);
                    return false;
                }
            } catch (Exception e) {
                ProfileHolder.clearProfile();
                throw e;
            }
        }
        return true;
    }
}
