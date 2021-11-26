package com.infilos.auth.token;

import com.infilos.auth.core.TokenProfileExtractor;
import com.infilos.auth.token.locate.*;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.*;
import org.pac4j.http.credentials.extractor.CookieExtractor;

import java.util.List;
import java.util.Optional;

/**
 * Extract token credentials from request context, depends on token locate.
 * 
 * See {@link TokenProfileExtractor} for details.
 */
public class TokenCredentialsExtractor implements CredentialsExtractor<TokenCredentials> {
    private final List<TokenLocate> tokenLocates;
    private final HeaderExtractor headerExtractor;
    private final CookieExtractor cookieExtractor;
    private final ParameterExtractor parameterExtractor;

    public TokenCredentialsExtractor(List<TokenLocate> tokenLocates, 
                                     HeaderTokenLocator header, 
                                     CookieTokenLocator cookie,
                                     ParameterTokenLocator parameter) {
        this.tokenLocates = tokenLocates;
        this.headerExtractor = new HeaderExtractor(header.getName(), header.getPrefix());
        this.headerExtractor.setTrimValue(header.isTrimValue());
        this.cookieExtractor = new CookieExtractor(cookie.getName());
        this.parameterExtractor = new ParameterExtractor(
            parameter.getName(),
            parameter.isSupportGetRequest(), 
            parameter.isSupportPostRequest()
        );
    }

    @Override
    public Optional<TokenCredentials> extract(WebContext context) {
        Optional<TokenCredentials> credentials = Optional.empty();

        for (TokenLocate locate : tokenLocates) {
            switch (locate) {
                case HEADER:
                    credentials = headerExtractor.extract(context);
                    break;
                case COOKIE:
                    credentials = cookieExtractor.extract(context);
                    break;
                case PARAMETER:
                    credentials = parameterExtractor.extract(context);
                    break;
            }

            if (credentials.isPresent()) {
                return credentials;
            }
        }

        return Optional.empty();
    }
}
