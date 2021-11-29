package com.infilos.auth.intercept.flow;

import com.infilos.auth.core.AuthorityConfig;
import com.infilos.auth.intercept.BaseFilter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.*;
import org.pac4j.core.matching.matcher.Matcher;

import java.util.List;
import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.assertTrue;

@Slf4j
@Setter
public class OuterLoginFilter extends BaseFilter {
    private Clients clients;
    private ClientFinder clientFinder = new OuterClientFinder();

    public OuterLoginFilter(Matcher pathMatcher) {
        super(pathMatcher);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected HttpAction process(AuthorityConfig config, JEEContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Access outer login: {}", context.getFullRequestURL());
        }
        
        final List<Client<?>> foundClients = clientFinder.find(this.clients, context, null);
        assertTrue(foundClients != null && foundClients.size() == 1,
            "Find indirect client for outer login failed: check the URL for a client name parameter");
        
        final Client foundClient = foundClients.get(0);
        log.debug("foundClient: {}", foundClient);
        assertNotNull("foundClient", foundClient);

        Optional<RedirectionAction> redirect = foundClient.getRedirectionAction(context);
        if (redirect.isPresent()) {
            return redirect.get();
        }
        return BadRequestAction.INSTANCE;
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public void initCheck() {
        assertNotNull("clients", clients);
    }
}
