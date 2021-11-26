package com.infilos.demo;

import com.infilos.auth.core.TokenProfile;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@EnableSpringHttpSession
public class Bootstrap {
    
    public static void main(String[] args){
        SpringApplication.run(Bootstrap.class, args);
    }

    @Bean
    public MapSessionRepository sessionRepository() {
        return new MapSessionRepository(new ConcurrentHashMap<>());
        
    }

    @Bean
    public Authorizer<TokenProfile> authorizer1() {
        return (context, profiles) -> {
            final TokenProfile profile = profiles.get(0);
            profile.setLinkedId("111222333444555666777888999000");
            return true;
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public LogAspect logAspect() {
        return new LogAspect();
    }
}
