package com.infilos.demo;

import com.infilos.auth.api.MatchPermit;
import com.infilos.auth.api.MatchRole;
import com.infilos.auth.core.ProfileHolder;
import com.infilos.auth.core.TokenProfile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
public class DemoController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/index")
    public String index() {
        TokenProfile profile = ProfileHolder.getProfile();
        System.out.println(profile.getId());
        System.out.println(profile.getToken());
        System.out.println(profile.getExpirationDate());
        System.out.println(profile.getIssuedAt());
        System.out.println(profile.getLinkedId());
        return "index";
    }

    @GetMapping("/a1")
    public String a1(Model model) {
        model.addAttribute("a", "a1");
        return "a";
    }

    @GetMapping("/a2")
    @MatchRole("admin")
    public String a2(Model model) {
        model.addAttribute("a", "a2");
        return "a";
    }

    @GetMapping("/a3")
    @MatchPermit("add")
    public String a3(HttpServletRequest request, Model model) {
        System.out.println(request);
        model.addAttribute("a", "a3");
        return "a";
    }

    @ResponseBody
    @PostMapping("/a4")
    public String a4() {
        return "a4_" + UUID.randomUUID();
    }
}
