package com.infilos.auth.intercept;

import com.infilos.auth.core.AuthorityConfig;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SpringMvcHandlerInterceptor implements HandlerInterceptor {
    private final AuthFilters filters;
    private final AuthorityConfig config;

    public SpringMvcHandlerInterceptor(AuthorityConfig config, AuthFilters filters) {
        this.config = config;
        this.filters = filters;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        return filters.execute(request, response, config);
    }
}
