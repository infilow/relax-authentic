package com.infilos.auth;

import com.infilos.auth.core.*;
import com.infilos.auth.intercept.AuthFilters;
import com.infilos.auth.intercept.flow.*;
import com.infilos.auth.intercept.handler.*;
import com.infilos.auth.token.*;
import com.infilos.auth.intercept.matching.PathOnlyMatcher;
import lombok.RequiredArgsConstructor;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.*;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.matching.matcher.PathMatcher;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AuthProperties.class)
@AutoConfigureBefore(AuthInterceptorAutoConfigure.class)
public class AuthConfigAutoConfigure {

    private final AuthProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public TokenModeSelector tokenModeSelector() {
        return new TokenModeSelector(properties.getTokenMode(), properties.getTokenSalt());
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenCredentialsExtractor tokenCredentialsExtractor() {
        return new TokenCredentialsExtractor(properties.getTokenLocates(), properties.getHeaderLocator(), properties.getCookieLocator(), properties.getParameterLocator());
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenGenerator tokenGenerator(TokenModeSelector tokenModeSelector) {
        return new TokenGenerator(tokenModeSelector);
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenProfileExtractor tokenProfileExtractor(TokenCredentialsExtractor credentialsExtractor,
                                                       TokenModeSelector tokenModeSelector) {
        return new TokenProfileExtractor(credentialsExtractor, tokenModeSelector);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthorityManager authorityManager() {
        return new AuthorityManager(properties.getSkipAuthRolePermission());
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthorityConfig authorityConfig(AuthorityManager authorityManager,
                                           TokenGenerator tokenGenerator,
                                           TokenProfileExtractor tokenProfileExtractor,
                                           ObjectProvider<ProfileStateManager> profileManager,
                                           ObjectProvider<LogoutHandler> logoutHandlerProvider,
                                           ObjectProvider<AjaxRequestResolver> ajaxRequestResolverProvider,
                                           ObjectProvider<Authorizer> authorizerProvider,
                                           ObjectProvider<Matcher> matcherProvider,
                                           ObjectProvider<HttpErrorHandler> httpActionHandlerProvider) {
        if (!properties.isStateless()) {
            Assert.hasText(properties.getLoginPath(), "loginPath must not blank when stateful");
        }
        
        AuthorityConfig config = new AuthorityConfig();
        config.setStateless(properties.isStateless());
        config.setEnableSession(properties.isSession());
        config.setTokenLocates(properties.getTokenLocates());
        config.setCookieTokenLocator(properties.getCookieLocator());
        config.setTokenExpiration(properties.getTokenExpire());
        config.setAuthorityManager(authorityManager);
        profileManager.ifAvailable(config::setProfileStateManager);
        logoutHandlerProvider.ifAvailable(config::setLogoutHandler);
        config.setLoginPath(properties.getLoginPath());
        config.setTokenGenerator(tokenGenerator);
        config.setTokenProfileExtractor(tokenProfileExtractor);
        config.appendMatcherNames(properties.getMatcherNames());
        config.appendAuthorizerNames(properties.getAuthorizerNames());
        matcherProvider.stream().forEach(config::addMatcher);
        authorizerProvider.stream().forEach(config::addAuthorizer);
        httpActionHandlerProvider.ifAvailable(config::setHttpErrorHandler);
        ajaxRequestResolverProvider.ifAvailable(config::setAjaxRequestResolver);
        
        return config;
    }

    @Bean
    @ConditionalOnMissingBean
    public Authentic securityManager(AuthorityConfig authorityConfig) {
        return new Authentic(authorityConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthFilters authInterceptorChain(AuthorityConfig authorityConfig,
                                            ObjectProvider<OuterCallbackHandler> callbackHandlerProvider,
                                            ObjectProvider<IndirectClient> indirectClientsProvider) {
        AuthFilters authFilters = new AuthFilters();

        /* AuthorityFilter begin */
        final PathMatcher securityPathMatcher = new PathMatcher();
        if (!CollectionUtils.isEmpty(properties.getExcludePath())) {
            properties.getExcludePath().forEach(securityPathMatcher::excludePath);
        }
        if (!CollectionUtils.isEmpty(properties.getExcludeBranch())) {
            properties.getExcludeBranch().forEach(securityPathMatcher::excludeBranch);
        }
        if (!CollectionUtils.isEmpty(properties.getExcludeRegex())) {
            properties.getExcludeBranch().forEach(securityPathMatcher::excludeRegex);
        }
        if (authorityConfig.getLoginPath() != null) {
            securityPathMatcher.excludePath(authorityConfig.getLoginPath());
        }
        final AuthorizeFilter securityFilter = new AuthorizeFilter(securityPathMatcher);
        authFilters.addFilter(securityFilter);
        /* AuthorityFilter end */

        /* logoutFilter begin */
        if (StringUtils.hasText(properties.getLogoutPath())) {
            authFilters.addFilter(new LogoutFilter(new PathOnlyMatcher(properties.getLogoutPath())));
        }
        /* logoutFilter end */

        /* other begin */
        if (!authorityConfig.isStateless()) {
            List<Client> indirectClients = indirectClientsProvider.stream().collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(indirectClients)) {
                final String outerLoginPath = properties.getOuterLoginPath();
                Assert.hasText(outerLoginPath, "outerLoginPath must not blank");

                final String outerCallbackPath = properties.getOuterCallbackPath();
                Assert.hasText(outerCallbackPath, "outerCallbackUrl must not blank");

                final OuterCallbackHandler outerCallbackHandler = callbackHandlerProvider.getIfAvailable();
                Assert.notNull(outerCallbackHandler, "callbackHandler must not null");

                final Clients clients = new Clients(outerCallbackPath, indirectClients);
                clients.setAjaxRequestResolver(authorityConfig.getAjaxRequestResolver());
                clients.setUrlResolver(new DefaultUrlResolver(true));

                final OuterLoginFilter sfLoginFilter = new OuterLoginFilter(new PathOnlyMatcher(outerLoginPath));
                sfLoginFilter.setClients(clients);
                authFilters.addFilter(sfLoginFilter);

                final OuterCallbackFilter outerCallbackFilter = new OuterCallbackFilter(new PathOnlyMatcher(outerCallbackPath));
                outerCallbackFilter.setClients(clients);
                outerCallbackFilter.setOuterCallbackHandler(outerCallbackHandler);
                authFilters.addFilter(outerCallbackFilter);
            }
        }
        /* other end */
        
        return authFilters;
    }
}
