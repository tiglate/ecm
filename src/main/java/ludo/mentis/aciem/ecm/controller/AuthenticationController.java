package ludo.mentis.aciem.ecm.controller;

import ludo.mentis.aciem.commons.web.FlashMessages;
import ludo.mentis.aciem.ecm.model.AuthenticationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AuthenticationController {

    @GetMapping("/login")
    public String login(
            @RequestParam(name = "loginRequired", required = false) final Boolean loginRequired,
            @RequestParam(name = "loginError", required = false) final Boolean loginError,
            final Model model) {
        model.addAttribute("authentication", new AuthenticationRequest());
        if (loginRequired == Boolean.TRUE) {
            model.addAttribute(FlashMessages.MSG_INFO, "Please login to access this area.");
        }
        if (loginError == Boolean.TRUE) {
            model.addAttribute(FlashMessages.MSG_ERROR, "Your login was not successful. Please try again.");
        }
        return "authentication/login";
    }

}
