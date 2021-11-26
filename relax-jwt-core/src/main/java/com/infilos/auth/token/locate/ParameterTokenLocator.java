package com.infilos.auth.token.locate;

import lombok.Data;
import org.pac4j.core.context.HttpConstants;

@Data
public class ParameterTokenLocator {
    /**
     * parameter's name
     */
    private String name = HttpConstants.AUTHORIZATION_HEADER;
    
    /**
     * if support GET
     */
    private boolean supportGetRequest = true;
    
    /**
     * if support POST
     */
    private boolean supportPostRequest = false;
}
