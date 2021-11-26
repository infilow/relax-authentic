package com.infilos.auth.error;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.http.RedirectionAction;

public class RedirectHttpAction extends RedirectionAction {

    protected RedirectHttpAction() {
        super(HttpConstants.FOUND);
    }
    
    public static final RedirectHttpAction INSTANCE = new RedirectHttpAction();
}
