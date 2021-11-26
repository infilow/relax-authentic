package com.infilos.auth;

import com.infilos.auth.core.AuthorityConfig;
import com.infilos.auth.intercept.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
public class AuthInterceptorAutoConfigure {
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(HandlerInterceptor.class)
    @ConditionalOnProperty(prefix = "relax.jwt", name = "filter", havingValue = "interceptor", matchIfMissing = true)
    public SpringMvcHandlerInterceptor springMvcHandlerInterceptor(AuthorityConfig authorityConfig, AuthFilters authFilters) {
        return new SpringMvcHandlerInterceptor(authorityConfig, authFilters);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(OncePerRequestFilter.class)
    @ConditionalOnProperty(prefix = "relax.jwt", name = "filter", havingValue = "web_filter")
    public ServletOncePerRequestFilter servletOncePerRequestFilter(AuthorityConfig authorityConfig, AuthFilters authFilters) {
        return new ServletOncePerRequestFilter(authorityConfig, authFilters);
    }

    @RequiredArgsConstructor
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnProperty(prefix = "relax.jwt", name = "filter", havingValue = "interceptor", matchIfMissing = true)
    public static class AuthorityWebMvcConfigurer implements WebMvcConfigurer {

        private final SpringMvcHandlerInterceptor springMvcHandlerInterceptor;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(springMvcHandlerInterceptor).addPathPatterns("/**");
        }
    }
}
