package com.infilos.demo;

import com.infilos.auth.intercept.handler.OuterCallbackHandler;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.UserProfile;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Bootstrap {
    
    public static void main(String[] args){
        SpringApplication.run(Bootstrap.class, args);
    }

    @Bean
    public CasClient casClient() {
        CasConfiguration configuration = new CasConfiguration("https://127.0.0.1/cas/login");
        return new CasClient(configuration);
    }

    @Bean
    public OuterCallbackHandler outerCallbackHandler() {
        return new OuterCallbackHandler() {
            @Override
            public HttpAction handle(JEEContext context, UserProfile profile) {
                System.out.println(profile);
                return null;
            }
        };
    }
    
}
