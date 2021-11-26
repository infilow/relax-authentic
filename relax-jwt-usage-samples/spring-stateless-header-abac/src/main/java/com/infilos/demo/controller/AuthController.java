package com.infilos.demo.controller;

import com.infilos.auth.Authentic;
import com.infilos.auth.core.TokenProfile;
import com.infilos.auth.error.AuthorizeException;
import com.infilos.demo.model.ProjectUser;
import com.infilos.demo.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RestController
@AllArgsConstructor
public class AuthController {
    
    private final Authentic authentic;
    private final UserService userService;

    @GetMapping("login")
    public String login(@RequestParam String user) {
        ProjectUser projectUser = userService.findUserByName(user);
        if (Objects.isNull(projectUser)) {
            throw new AuthorizeException("User not found: " + user);
        }
        
        TokenProfile profile = new TokenProfile();
        profile.setId("111111111111");
        profile.setLinkedId("22222222222");
        profile.setDisplayName(user);
        profile.addRole(projectUser.getRole().name());
        
        return authentic.login(profile);
    }
}
