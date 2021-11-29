package com.infilos.demo;

import com.infilos.auth.Authentic;
import com.infilos.auth.core.TokenProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final Authentic authentic;

    public void login() {
        TokenProfile profile = new TokenProfile();
        profile.setId("111111111111");
        profile.addRole("admin");
        profile.addPermission("add");
        profile.addPermission("xx");
        
        authentic.login(profile);
    }
}
