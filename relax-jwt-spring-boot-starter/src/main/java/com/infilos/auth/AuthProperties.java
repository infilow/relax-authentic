package com.infilos.auth;

import com.infilos.auth.intercept.FilterMode;
import com.infilos.auth.profile.ProfileMode;
import com.infilos.auth.token.TokenLocate;
import com.infilos.auth.token.TokenMode;
import com.infilos.auth.token.locate.*;
import lombok.Data;
import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.matching.matcher.DefaultMatchers;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.*;

@Data
@ConfigurationProperties("relax.jwt")
public class AuthProperties {

    // enable authority aspect
    private final AspectAnnotation aspect = new AspectAnnotation();

    // execute login out of current server
    private boolean stateless = true;

    // enable pac4j session
    private boolean session = false;

    // token generate mode
    private TokenMode tokenMode = TokenMode.DEFAULT;

    // token generate salt
    private String tokenSalt = UUID.randomUUID().toString().replace("-", "");

    // duration spec: PnDTnHnMn.nS
    private String tokenExpire;

    // jwt's token location
    private List<TokenLocate> tokenLocates = Collections.singletonList(TokenLocate.HEADER);

    // jwt's token location: from header
    @NestedConfigurationProperty
    private final HeaderTokenLocator headerLocator = new HeaderTokenLocator();

    // jwt's token location: from cookie
    @NestedConfigurationProperty
    private final CookieTokenLocator cookieLocator = new CookieTokenLocator();

    // jwt's token location: from parameter
    @NestedConfigurationProperty
    private final ParameterTokenLocator parameterLocator = new ParameterTokenLocator();

    // pac4j matcher names
    private String matcherNames = DefaultMatchers.NONE;

    // pac4j authorizer names
    private String authorizerNames = DefaultAuthorizers.NONE;

    // skip authorize when meet this role/permission
    private String skipAuthRolePermission = "admin-role-permission";

    // exclude request path
    private List<String> excludePath;

    // exclude request path prefix
    private List<String> excludeBranch;

    // exclude request path regex
    private List<String> excludeRegex;

    // loign path when require authorize, used when execute login in current server, auto exclude
    private String loginPath;

    // request this path will execute logout, then redirect to loginPath
    private String logoutPath;

    /**
     * request this path will redirect to outer url to execute login, should provide {@link IndirectClient}
     */
    private String outerLoginPath;

    /**
     * callback url after execute outer login, should provide {@link IndirectClient}
     */
    private String outerCallbackPath;

    // intercept mode
    private FilterMode filter = FilterMode.INTERCEPTOR;

    // profile context mode
    private ProfileMode profileMode = ProfileMode.REQUEST;

    @Data
    public static class AspectAnnotation {
        private boolean enabled = true;
    }
}
