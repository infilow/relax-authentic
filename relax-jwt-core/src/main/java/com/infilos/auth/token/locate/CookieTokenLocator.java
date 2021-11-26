package com.infilos.auth.token.locate;

import lombok.Data;
import org.pac4j.core.context.Cookie;
import org.pac4j.core.util.Pac4jConstants;

@Data
public class CookieTokenLocator {
    private String name = Pac4jConstants.SESSION_ID;
    private int version = 0;
    private String comment;
    private String domain = "";
    private String path = Pac4jConstants.DEFAULT_URL_VALUE;
    private boolean secure;
    private boolean isHttpOnly;

    /**
     * Cookie from pac4j
     */
    public Cookie getCookie(final String token, int maxAge) {
        Cookie cookie = new Cookie(name, token);
        cookie.setVersion(version);
        cookie.setSecure(secure);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(isHttpOnly);
        cookie.setComment(comment);
        cookie.setDomain(domain);
        return cookie;
    }

    public Cookie clean() {
        return getCookie("", 0);
    }
}
