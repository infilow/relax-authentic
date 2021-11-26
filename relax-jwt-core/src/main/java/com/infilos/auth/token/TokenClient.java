package com.infilos.auth.token;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;

/**
 * Profile extractor wrapper for Pac4j, used to extract profile and credentials.
 */
public class TokenClient extends DirectClient<TokenCredentials> {

    public TokenClient(final CredentialsExtractor<TokenCredentials> credentialsExtractor,
                       final JwtAuthenticator tokenAuthenticator) {
        defaultCredentialsExtractor(credentialsExtractor);
        defaultAuthenticator(tokenAuthenticator);
    }

    @Override
    protected void clientInit() {
    }
}
