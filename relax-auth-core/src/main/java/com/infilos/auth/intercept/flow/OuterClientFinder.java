package com.infilos.auth.intercept.flow;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.Pac4jConstants;

import java.util.*;

/**
 * Outer client finder used to request outer login and accept outer callback after login.
 */
@Slf4j
@Setter
public class OuterClientFinder implements ClientFinder {

    private String clientNameParameter = Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER;

    @Override
    @SuppressWarnings("rawtypes")
    public List<Client<? extends Credentials>> find(final Clients clients, final WebContext context, final String clientNames) {
        final List<Client<? extends Credentials>> result = new ArrayList<>();
        final Optional<String> clientNameOnRequest = context.getRequestParameter(clientNameParameter);
        log.debug("clientNameOnRequest: {}", clientNameOnRequest);
        
        if (clientNameOnRequest.isPresent()) {
            // from the request
            final Optional<Client> client = clients.findClient(clientNameOnRequest.get());
            client.ifPresent(result::add);
        }
        
        return result;
    }
}
