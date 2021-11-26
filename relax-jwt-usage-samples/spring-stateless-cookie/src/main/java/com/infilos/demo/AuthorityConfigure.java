package com.infilos.demo;

import com.infilos.auth.core.ProfileStateManager;
import com.infilos.auth.core.TokenProfile;
import com.infilos.auth.token.TokenModeSelector;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import org.pac4j.jwt.config.encryption.RSAEncryptionConfiguration;
import org.pac4j.jwt.config.signature.RSASignatureConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.*;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AuthorityConfigure {

    public static final Map<String, String> tokenCache = new HashMap<>();

    private static final KeyPair keyPair = initKeyPair();

    static KeyPair initKeyPair() {
        final KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public TokenModeSelector tokenModeSelector() {
        return new TokenModeSelector(
            new RSASignatureConfiguration(keyPair),
            new RSAEncryptionConfiguration(keyPair, JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128GCM)
        );
    }

    @Bean
    public ProfileStateManager profileStateManager() {
        return new ProfileStateManager() {
            @Override
            public void active(TokenProfile profile) {
                tokenCache.put(profile.getId(), profile.getToken());
            }

            @Override
            public boolean isOnline(TokenProfile profile) {
                String token = tokenCache.get(profile.getId());
                return profile.getToken().equals(token);
            }

            @Override
            public void discard(TokenProfile profile) {
                tokenCache.remove(profile.getId());
            }
        };
    }
}
