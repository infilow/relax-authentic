package com.infilos.auth.token;

import com.infilos.auth.core.TokenProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;

public class TokenModeSelector extends InitializableObject {
    private JwtAuthenticator jwtAuthenticator;
    private SignatureConfiguration signatureConfiguration;
    private EncryptionConfiguration encryptionConfiguration;

    public TokenModeSelector() {
        this(TokenMode.PLAINTEXT, null);
    }

    public TokenModeSelector(TokenMode type, String sign) {
        if (type != null && type != TokenMode.PLAINTEXT) {
            CommonHelper.assertNotBlank(sign, "sign");
            if (type == TokenMode.DEFAULT) {
                this.signatureConfiguration = new SecretSignatureConfiguration(sign);
                this.encryptionConfiguration = new SecretEncryptionConfiguration(sign);
            } else if (type == TokenMode.ENCRYPTION) {
                this.encryptionConfiguration = new SecretEncryptionConfiguration(sign);
            } else if (type == TokenMode.SIGNATURE) {
                this.signatureConfiguration = new SecretSignatureConfiguration(sign);
            }
        }
    }

    public TokenModeSelector(SignatureConfiguration signatureConfiguration) {
        this(signatureConfiguration, null);
    }

    public TokenModeSelector(EncryptionConfiguration encryptionConfiguration) {
        this(null, encryptionConfiguration);
    }

    public TokenModeSelector(SignatureConfiguration signatureConfiguration, EncryptionConfiguration encryptionConfiguration) {
        this.signatureConfiguration = signatureConfiguration;
        this.encryptionConfiguration = encryptionConfiguration;
    }

    @Override
    protected void internalInit() {
        this.jwtAuthenticator = new JwtAuthenticator();
        if (this.signatureConfiguration != null) {
            jwtAuthenticator.setSignatureConfiguration(this.signatureConfiguration);
        }
        if (this.encryptionConfiguration != null) {
            jwtAuthenticator.setEncryptionConfiguration(this.encryptionConfiguration);
        }
    }

    public JwtGenerator<TokenProfile> getGenerator() {
        init();
        return new TokenModeGenerator(signatureConfiguration, encryptionConfiguration);
    }

    public JwtAuthenticator getAuthenticator() {
        init();
        return jwtAuthenticator;
    }
}
