package com.infilos.abac.api;

import java.util.List;

public interface PolicyRepository {
    
    /**
     * Load all policy rules from storage.
     */
    List<PolicyRule> findAllPolicyRules();
}
