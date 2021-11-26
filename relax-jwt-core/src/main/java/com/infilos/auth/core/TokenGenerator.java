package com.infilos.auth.core;

import com.infilos.auth.token.TokenModeSelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.jwt.profile.JwtGenerator;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class TokenGenerator {
    private final TokenModeSelector modeSelector;

    /**
     * Generate token from user profile with expiration.
     */
    public String generate(TokenProfile profile, String expireTime) {
        JwtGenerator<TokenProfile> jwtGenerator = modeSelector.getGenerator();

        if (CommonHelper.isNotBlank(expireTime)) {
            jwtGenerator.setExpirationTime(expireAfter(expireTime));
        }
        
        String jwt = jwtGenerator.generate(profile);
        
        final int length = jwt.length();
        if (length > 3072) {
            log.warn("JWT length is {}, longer 3072, be careful!", length);
        }
        
        return jwt;
    }

    private Date expireAfter(String expiration) {
        Duration duration = Duration.parse(expiration);
        LocalDateTime expireAt = LocalDateTime.now().plus(duration.toMillis(), ChronoUnit.MILLIS);

        return Date.from(expireAt.atZone(ZoneId.systemDefault()).toInstant());
    }
}
