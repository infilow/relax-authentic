package com.infilos.auth.intercept.handler;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.UserProfile;

/**
 * Process TokenProfile after outer login, eg. execute AuthorityManager#login
 */
public interface OuterCallbackHandler {

    HttpAction handle(JEEContext context, UserProfile profile);
}
