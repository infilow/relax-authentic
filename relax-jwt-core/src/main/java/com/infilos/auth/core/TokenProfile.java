package com.infilos.auth.core;

import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.jwt.AbstractJwtProfile;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.pac4j.core.util.Pac4jConstants;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.List;
import java.util.Locale;

/**
 * User profile to generate token, then extract from token.
 */
public class TokenProfile extends AbstractJwtProfile {

    private String token;

    public TokenProfile() {
    }

    public TokenProfile(String id) {
        this.setId(id);
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        Assert.isNull(this.token, "token can only be set once");
        Assert.hasText(token, "token cannot be black");
        this.token = token;
    }

    public void setTenant(String tenant) {
        addAttribute("tenant", tenant);
    }

    public void setIssuer(String iss) {
        addAttribute(JwtClaims.ISSUER, iss);
    }

    public void setAudience(List<String> aud) {
        addAttribute(JwtClaims.AUDIENCE, aud);
    }

    public void setEmail(String email) {
        addAttribute(CommonProfileDefinition.EMAIL, email);
    }

    public void setFirstName(String firstName) {
        addAttribute(CommonProfileDefinition.FIRST_NAME, firstName);
    }

    public void setFamilyName(String familyName) {
        addAttribute(CommonProfileDefinition.FAMILY_NAME, familyName);
    }

    public void setDisplayName(String displayName) {
        addAttribute(CommonProfileDefinition.DISPLAY_NAME, displayName);
    }

    public void setUsername(String username) {
        addAttribute(Pac4jConstants.USERNAME, username);
    }

    public void setGender(Gender gender) {
        addAttribute(CommonProfileDefinition.GENDER, gender.name());
    }

    public void setLocale(Locale locale) {
        addAttribute(CommonProfileDefinition.LOCALE, locale.toLanguageTag());
    }

    public void setPictureUrl(URI pictureUrl) {
        addAttribute(CommonProfileDefinition.PICTURE_URL, pictureUrl.toASCIIString());
    }

    public void setProfileUrl(URI profileUrl) {
        addAttribute(CommonProfileDefinition.PROFILE_URL, profileUrl.toASCIIString());
    }

    public void setLocation(String location) {
        addAttribute(CommonProfileDefinition.LOCATION, location);
    }

    public void setMobilePhone(String mobilePhone) {
        addAttribute("mobile_phone", mobilePhone);
    }

    @Override
    public Gender getGender() {
        String attribute = getAttribute(CommonProfileDefinition.GENDER, String.class);
        if (attribute == null) {
            return Gender.UNSPECIFIED;
        }
        return Gender.valueOf(attribute);
    }

    @Override
    public Locale getLocale() {
        String attribute = getAttribute(CommonProfileDefinition.LOCALE, String.class);
        return attribute == null ? null : Locale.forLanguageTag(attribute);
    }

    @Override
    public URI getPictureUrl() {
        String attribute = getAttribute(CommonProfileDefinition.PICTURE_URL, String.class);
        return attribute == null ? null : URI.create(attribute);
    }

    @Override
    public URI getProfileUrl() {
        String attribute = getAttribute(CommonProfileDefinition.PROFILE_URL, String.class);
        return attribute == null ? null : URI.create(attribute);
    }
    
    public String getMobilePhone() {
        return getAttribute("mobile_phone", String.class);
    }

    public String getTenant() {
        return getAttribute("tenant", String.class);
    }
}
