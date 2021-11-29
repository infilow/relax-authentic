package com.infilos.auth.intercept.flow;

import com.infilos.auth.core.AuthorityConfig;
import com.infilos.auth.intercept.BaseFilter;
import com.infilos.auth.intercept.handler.OuterCallbackHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.BadRequestAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Slf4j
@Setter
public class OuterCallbackFilter extends BaseFilter {

    private Clients clients;
    private OuterCallbackHandler outerCallbackHandler;
    private ClientFinder clientFinder = new DefaultCallbackClientFinder();

    public OuterCallbackFilter(Matcher pathMatcher) {
        super(pathMatcher);
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public void initCheck() {
        CommonHelper.assertNotNull("clients", clients);
        CommonHelper.assertNotNull("callbackHandler", outerCallbackHandler);
    }
    
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected HttpAction process(AuthorityConfig config, JEEContext context) {
        final List<Client<?>> foundClients = clientFinder.find(this.clients, context, null);
        Assert.isTrue(foundClients != null && foundClients.size() == 1,
            "unable to find one indirect client for the callback: check the callback URL for a client name parameter");
        
        final Client foundClient = foundClients.get(0);
        log.debug("foundClient: {}", foundClient);
        
        Assert.notNull(foundClient, "foundClient cannot be null");
        final Optional<Credentials> credentials = foundClient.getCredentials(context);
        log.debug("credentials: {}", credentials);
        
        if (credentials.isPresent()) {
            final Optional<UserProfile> profile = foundClient.getUserProfile(credentials.get(), context);
            log.debug("profile: {}", profile);
            if (profile.isPresent()) {
                return outerCallbackHandler.handle(context, profile.get());
            }
        }
        return BadRequestAction.INSTANCE;
    }
}
