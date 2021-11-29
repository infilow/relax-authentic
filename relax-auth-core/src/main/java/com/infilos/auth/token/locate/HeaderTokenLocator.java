package com.infilos.auth.token.locate;

import lombok.Data;
import org.pac4j.core.context.HttpConstants;

@Data
public class HeaderTokenLocator {
    
    /**
     * header's name
     */
    private String name = HttpConstants.AUTHORIZATION_HEADER;
    
    /**
     * herder's name prefix
     */
    private String prefix = "";
    
    /**
     * if trim blank
     */
    private boolean trimValue;
}
