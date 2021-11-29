package com.infilos.auth;

import com.infilos.auth.aop.StaticMethodAuthorityAdvisor;
import com.infilos.auth.aop.MethodAuthorityInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "relax.jwt.aspect.enabled", havingValue = "true", matchIfMissing = true)
public class AuthAspectAutoConfigure {
    
    @Bean
    public MethodAuthorityInterceptor methodAuthorityInterceptor() {
        return new MethodAuthorityInterceptor();
    }

    @Bean
    public StaticMethodAuthorityAdvisor staticMethodAuthorityAdvisor(MethodAuthorityInterceptor interceptor) {
        StaticMethodAuthorityAdvisor advisor = new StaticMethodAuthorityAdvisor();
        advisor.setAdvice(interceptor);
        return advisor;
    }
}
