package com.infilos.abac.config;

import com.infilos.abac.core.MethodAbacAuthorityInterceptor;
import com.infilos.abac.core.StaticMethodAbacAuthorityAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "relax.jwt.aspect.enabled", havingValue = "true", matchIfMissing = true)
public class AbacAspectAutoConfigure {

    @Bean
    public MethodAbacAuthorityInterceptor methodAbacAuthorityInterceptor() {
        return new MethodAbacAuthorityInterceptor();
    }

    @Bean
    public StaticMethodAbacAuthorityAdvisor staticMethodAbacAuthorityAdvisor(MethodAbacAuthorityInterceptor interceptor) {
        StaticMethodAbacAuthorityAdvisor advisor = new StaticMethodAbacAuthorityAdvisor();
        advisor.setAdvice(interceptor);
        return advisor;
    }
}
