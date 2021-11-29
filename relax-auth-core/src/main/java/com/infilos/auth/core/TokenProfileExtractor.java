package com.infilos.auth.core;

import com.infilos.auth.token.*;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;

import java.util.Optional;

@Slf4j
public class TokenProfileExtractor {
    private final TokenClient tokenClient;

    public TokenProfileExtractor(TokenCredentialsExtractor credentialsExtractor,
                                 TokenModeSelector modeSelector) {
        this.tokenClient = new TokenClient(credentialsExtractor, modeSelector.getAuthenticator());
    }

    /**
     * Extract user profile from request context.
     */
    public TokenProfile extract(JEEContext context) {
        TokenCredentials credentials = tokenClient.getCredentials(context).orElse(null);
        if (credentials == null) {
            return null;
        }

        Optional<UserProfile> profile = tokenClient.getUserProfile(credentials, context);

        if (profile.isPresent()) {
            CommonProfile commonProfile = (CommonProfile) profile.get();
            TokenProfile tokenProfile;
            if (commonProfile instanceof TokenProfile) {
                tokenProfile = (TokenProfile) commonProfile;
            } else {
                tokenProfile = new TokenProfile();
                tokenProfile.setId(commonProfile.getId());
                tokenProfile.addRoles(commonProfile.getRoles());
                tokenProfile.addPermissions(commonProfile.getPermissions());
                tokenProfile.addAttributes(commonProfile.getAttributes());
            }
            tokenProfile.setToken(credentials.getToken());
            
            log.debug("Extract token profile: roles-{}, permits-{}", tokenProfile.getRoles(), tokenProfile.getPermissions());
            
            return tokenProfile;
        }

        return null;
    }
}
