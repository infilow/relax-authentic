package com.infilos.demo;

import com.infilos.auth.api.MatchPermit;
import com.infilos.auth.api.MatchRole;
import com.infilos.auth.Authentic;
import com.infilos.auth.core.TokenProfile;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
public class DemoController {
    private final Authentic authentic;
    
    @GetMapping("login")
    public String login() {
        TokenProfile profile = new TokenProfile();
        profile.setId("111111111111");
        profile.setLinkedId("22222222222");
        profile.addRole("admin");
        profile.addPermission("add");
        return authentic.login(profile);
    }

    @GetMapping("a1")
    public String a1() {
        return "a1";
    }

    @GetMapping("a2")
    @MatchRole("admin")
    public String a2() {
        return "a2";
    }

    @GetMapping("a3")
    @MatchPermit("add")
    public String a3() {
        return "a3";
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
            .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
            .getHandlerMethods();
        map.forEach((key, value) -> log.info("{} {}", key, value));
    }
}
