package com.infilos.abac.core;

import com.infilos.abac.api.PolicyEvaluator;
import com.infilos.abac.api.PolicyVerifier;
import com.infilos.auth.core.ProfileHolder;
import com.infilos.auth.core.TokenProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.exception.http.ForbiddenAction;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class AwarePolicyVerifier implements PolicyVerifier {
    private final PolicyEvaluator policyEvaluator;

    @Override
    public void verify(Object resource, String action) {
        final TokenProfile profile = ProfileHolder.getProfile();
        final Map<String, Object> environ = EnvironBuilder.create().build();

        boolean ignoredOrAllowed = policyEvaluator.evaluate(profile, resource, action, environ);

        if (!ignoredOrAllowed) {
            throw ForbiddenAction.INSTANCE;
        }
    }

    @Override
    public <T extends Enum<T>> void verify(Object resource, T action) {
        verify(resource, action.name());
    }
}
