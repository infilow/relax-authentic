package com.infilos.abac.core;

import com.infilos.abac.api.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationException;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class BasePolicyEvaluator implements PolicyEvaluator {

    private final PolicyRepository policyRepository;

    /**
     * @return true if no matched policy to check, or any policy allowed, false means none policy allowed
     */
    @Override
    public boolean evaluate(Object profile, Object resource, Object action, Object environ) {
        List<PolicyRule> allRules = policyRepository.findAllPolicyRules();
        AccessContext accessContext = new AccessContext(profile, resource, action, environ);
        List<PolicyRule> matchedRules = filterMatchedRules(allRules, accessContext);

        // no need to check policy
        if (matchedRules.isEmpty()) {
            return true;
        }

        return evaluateMatchedRules(matchedRules, accessContext);
    }

    private List<PolicyRule> filterMatchedRules(List<PolicyRule> rules, AccessContext context) {
        List<PolicyRule> matchedRules = new ArrayList<>();
        for (PolicyRule rule : rules) {
            try {
                Boolean matches = rule.getMatcher().getValue(context, Boolean.class);
                if (Objects.equals(Boolean.TRUE, matches)) {
                    matchedRules.add(rule);
                }
            } catch (EvaluationException ex) {
                log.info("An error occurred while evaluating PolicyRule.", ex);
            }
        }

        return matchedRules;
    }

    private boolean evaluateMatchedRules(List<PolicyRule> rules, AccessContext context) {
        for (PolicyRule rule : rules) {
            try {
                Boolean evaluated = rule.getVerifier().getValue(context, Boolean.class);
                if (Objects.equals(Boolean.TRUE, evaluated)) {
                    return true;
                }
            } catch (EvaluationException ex) {
                log.warn("An error occurred while evaluating PolicyRule.", ex);
            }
        }

        return false;
    }
}
