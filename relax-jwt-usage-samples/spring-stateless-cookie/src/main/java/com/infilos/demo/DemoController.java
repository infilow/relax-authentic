package com.infilos.demo;

import com.infilos.auth.api.MatchPermit;
import com.infilos.auth.api.MatchRole;
import com.infilos.auth.core.ProfileHolder;
import com.infilos.auth.Authentic;
import com.infilos.auth.core.TokenProfile;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class DemoController {
    private final Authentic authentic;

    @GetMapping("login")
    public String login() {
        TokenProfile profile = new TokenProfile();
        profile.setId("111111111111");
        profile.addRole("admin");

        return authentic.login(profile, false);
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

    @GetMapping("xx")
    public String xx() {
        AuthorityConfigure.tokenCache.remove(ProfileHolder.getProfile().getId());
        return "xx";
    }
}
