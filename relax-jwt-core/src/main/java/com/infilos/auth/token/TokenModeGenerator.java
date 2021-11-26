package com.infilos.auth.token;

import com.infilos.auth.core.TokenProfile;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.Getter;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;

import java.util.Date;

public class TokenModeGenerator extends JwtGenerator<TokenProfile> {

    @Getter
    private Date expiration;

    public TokenModeGenerator() {
    }

    public TokenModeGenerator(SignatureConfiguration signatureConfiguration) {
        super(signatureConfiguration);
    }

    public TokenModeGenerator(SignatureConfiguration signatureConfiguration,
                              EncryptionConfiguration encryptionConfiguration) {
        super(signatureConfiguration, encryptionConfiguration);
    }

    @Override
    protected JWTClaimsSet buildJwtClaimsSet(TokenProfile profile) {
        // claims builder with subject and issue time
        final Date issueAt = new Date();
        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
            .issueTime(issueAt);

        // add attributes
        profile.getAttributes().forEach(builder::claim);
        builder.claim(INTERNAL_ROLES, profile.getRoles());
        builder.claim(INTERNAL_PERMISSIONS, profile.getPermissions());
        builder.claim(INTERNAL_LINKEDID, profile.getLinkedId());

        builder.subject(profile.getTypedId());
        if (expiration != null) {
            builder.expirationTime(expiration);
            profile.addAttribute(JwtClaims.EXPIRATION_TIME, expiration);
        }
        profile.addAttribute(JwtClaims.ISSUED_AT, issueAt);

        // claims
        return builder.build();
    }

    @Override
    public void setExpirationTime(final Date expirationTime) {
        this.expiration = new Date(expirationTime.getTime());
    }
}
