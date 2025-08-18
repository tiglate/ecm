package ludo.mentis.aciem.ecm.controller;

import ludo.mentis.aciem.ecm.util.FlashMessages;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("isAuthenticated()")
public class HomeController {

    @GetMapping("/")
    public String index(@RequestParam(name = "logoutSuccess", required = false) final Boolean logoutSuccess,
                        final Model model) {
        if (logoutSuccess == Boolean.TRUE) {
            model.addAttribute(FlashMessages.MSG_INFO, "Your logout was successful.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> authorities = authentication == null ? List.of() : authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .collect(Collectors.toList());
        model.addAttribute("authorities", authorities);

        return "home/index";
    }

}
