package com.infilos.auth.intercept;

import com.infilos.auth.core.AuthorityConfig;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.HttpAction;

public interface AuthFilter {

    /**
     * smaller will be process earlier
     */
    default int order() {
        return 0;
    }

    default void initCheck() {
    }

    HttpAction filter(AuthorityConfig config, JEEContext context);    
}
