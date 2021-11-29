package com.infilos.auth.core;

import com.infilos.auth.intercept.handler.HttpErrorHandler;
import com.infilos.auth.intercept.handler.LogoutHandler;
import com.infilos.auth.token.TokenLocate;
import com.infilos.auth.token.locate.CookieTokenLocator;
import lombok.*;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.matching.checker.DefaultMatchingChecker;
import org.pac4j.core.matching.checker.MatchingChecker;
import org.pac4j.core.matching.matcher.DefaultMatchers;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Data
@SuppressWarnings("rawtypes")
public class AuthorityConfig {
    /**
     * separate frontend and backend
     */
    private boolean stateless = true;

    /**
     * if session enabled
     */
    private boolean enableSession = false;

    /**
     * where and how to attach or extract token
     */
    private List<TokenLocate> tokenLocates = Collections.singletonList(TokenLocate.HEADER);
    
    /**
     * token expiration, duration spec, see {@link java.time.Duration#parse}
     */
    private String tokenExpiration;
    
    /**
     * token generator depends on generate mode
     */
    private TokenGenerator tokenGenerator;
    
    /**
     * token profile extractor
     */
    private TokenProfileExtractor tokenProfileExtractor;
    
    /**
     * profile state manager, default do nothing
     */
    private ProfileStateManager profileStateManager = ProfileStateManager.NOOP;
    
    /**
     * check user roles/permissions depends on profile
     */
    private AuthorityManager authorityManager;

    private CookieTokenLocator cookieTokenLocator;

    private LogoutHandler logoutHandler = new LogoutHandler.Default();

    private HttpErrorHandler httpErrorHandler = new HttpErrorHandler.Default();

    private ClientFinder clientFinder = new DefaultCallbackClientFinder();

    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();

    /**
     * MatchingChecker
     */
    private MatchingChecker matchingChecker = new DefaultMatchingChecker();
    
    /**
     * AuthorizationChecker
     */
    private AuthorizationChecker authorizationChecker = new DefaultAuthorizationChecker();

    /**
     * pac4j matchers, see {@link DefaultMatchingChecker}
     */
    @Setter(AccessLevel.NONE)
    private String matcherNames = DefaultMatchers.NONE;
    
    /**
     * pac4j authorizers, see {@link DefaultAuthorizationChecker}
     */
    @Setter(AccessLevel.NONE)
    private String authorizerNames = DefaultAuthorizers.NONE;

    /**
     * pac4j matchers
     */
    @Setter(AccessLevel.NONE)
    private Map<String, Matcher> matchers = new HashMap<>();
    
    /**
     * pac4j authorizers
     */
    @Setter(AccessLevel.NONE)
    private Map<String, Authorizer> authorizers = new HashMap<>();
    
    private String loginPath;

    public void addAuthorizer(Authorizer authorizer) {
        if (authorizer != null) {
            String key = authorizer.getClass().getSimpleName();
            authorizers.put(key, authorizer);
            this.appendAuthorizerNames(key);
        }
    }

    public void addAuthorizers(Collection<Authorizer> authorizers) {
        if (!CollectionUtils.isEmpty(authorizers)) {
            for (Authorizer<?> authorizer : authorizers) {
                addAuthorizer(authorizer);
            }
        }
    }

    public void appendAuthorizerNames(String value) {
        if (CommonHelper.isNotBlank(value)) {
            if (authorizerNames == null) {
                authorizerNames = value;
            } else {
                if (DefaultAuthorizers.NONE.equals(authorizerNames)) {
                    authorizerNames = value;
                } else {
                    if (!DefaultAuthorizers.NONE.equals(value)) {
                        authorizerNames = authorizerNames.concat(Pac4jConstants.ELEMENT_SEPARATOR).concat(value);
                    }
                }
            }
        }
    }

    public void addMatcher(Matcher matcher) {
        if (matcher != null) {
            String key = matcher.getClass().getSimpleName();
            matchers.put(key, matcher);
            this.appendMatcherNames(key);
        }
    }

    public void addMatchers(Collection<Matcher> matchers) {
        if (!CollectionUtils.isEmpty(matchers)) {
            for (Matcher matcher : matchers) {
                addMatcher(matcher);
            }
        }
    }

    public void appendMatcherNames(String value) {
        if (CommonHelper.isNotBlank(value)) {
            if (matcherNames == null) {
                matcherNames = value;
            } else {
                if (DefaultMatchers.NONE.equals(matcherNames)) {
                    matcherNames = value;
                } else {
                    if (!DefaultMatchers.NONE.equals(value)) {
                        matcherNames = matcherNames.concat(Pac4jConstants.ELEMENT_SEPARATOR).concat(value);
                    }
                }
            }
        }
    }

    public boolean checkMatching(JEEContext context) {
        return matchingChecker.matches(context, matcherNames, matchers, Collections.emptyList());
    }

    public boolean checkAuthorization(JEEContext context, TokenProfile profile) {
        return authorizationChecker.isAuthorized(context, Collections.singletonList(profile), authorizerNames, authorizers, Collections.emptyList());
    }
    
    public boolean enabledCookieToken() {
        return tokenLocates.stream().anyMatch(l -> l == TokenLocate.COOKIE);
    }
}
