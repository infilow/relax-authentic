package com.infilos.abac.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.infilos.abac.api.*;
import com.infilos.abac.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(AbacAspectAutoConfigure.class)
public class AbacConfigAutoConfigure {

    @Value("${relax.jwt.abac.policy:abac-policy.json}")
    private String policyFilePath;

    @Bean
    @ConditionalOnMissingBean
    public PolicyRepository policyRepository() {
        return new JsonPolicyRepository(new ObjectMapper(), policyFilePath);
    }

    @Bean
    @ConditionalOnMissingBean
    public PolicyEvaluator policyEvaluator(PolicyRepository policyRepository) {
        return new BasePolicyEvaluator(policyRepository);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public PolicyVerifier policyVerifier(PolicyEvaluator policyEvaluator) {
        return new AwarePolicyVerifier(policyEvaluator);
    }
}
