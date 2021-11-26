package com.infilos.demo;

import com.infilos.auth.api.MatchPermit;
import com.infilos.auth.api.MatchRole;
import com.infilos.auth.core.ProfileHolder;
import com.infilos.auth.core.TokenProfile;
import com.infilos.auth.intercept.context.WebContext;
import lombok.RequiredArgsConstructor;
import org.pac4j.core.util.Pac4jConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class DemoController {
    
    private final LoginService loginService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/auth/login")
    public String alogin() {
        loginService.login();
        
        return "redirect:/index";
    }

    @LogMark
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

    @LogMark
    @GetMapping("/a1")
    public String a1(Model model) {
        model.addAttribute("a", "a1");
        model.addAttribute("csrf", WebContext.getJEEContext(false).getRequestAttribute(Pac4jConstants.CSRF_TOKEN).orElse(null));
        
        return "a";
    }

    @LogMark
    @GetMapping("/a2")
    @MatchRole("admin")
    public String a2(Model model) {
        model.addAttribute("a", "a2");
        model.addAttribute("csrf", WebContext.getJEEContext(false).getRequestAttribute(Pac4jConstants.CSRF_TOKEN).orElse(null));
        return "a";
    }

    @LogMark
    @GetMapping("/a3")
    @MatchPermit("add")
    public String a3(HttpServletRequest request, Model model) {
        System.out.println(request);
        model.addAttribute("a", "a3");
        model.addAttribute("csrf", WebContext.getJEEContext(false).getRequestAttribute(Pac4jConstants.CSRF_TOKEN).orElse(null));
        return "a";
    }

    @ResponseBody
    @PostMapping("/a4")
    public String a4() {
        return "a4_" + UUID.randomUUID();
    }
}
