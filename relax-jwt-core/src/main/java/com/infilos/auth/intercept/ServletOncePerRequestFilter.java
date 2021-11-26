package com.infilos.auth.intercept;

import com.infilos.auth.core.AuthorityConfig;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletOncePerRequestFilter extends OncePerRequestFilter {
    private final AuthFilters filters;
    private final AuthorityConfig config;

    public ServletOncePerRequestFilter(AuthorityConfig config, AuthFilters filters) {
        this.config = config;
        this.filters = filters;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        boolean result = filters.execute(request, response, config);
        if (result) {
            chain.doFilter(request, response);
        }
    }
}
