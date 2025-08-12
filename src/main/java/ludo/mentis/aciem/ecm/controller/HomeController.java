package ludo.mentis.aciem.ecm.controller;

import ludo.mentis.aciem.ecm.util.FlashMessages;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {

    @GetMapping("/")
    public String index(@RequestParam(name = "logoutSuccess", required = false) final Boolean logoutSuccess,
                        final Model model) {
        if (logoutSuccess == Boolean.TRUE) {
            model.addAttribute(FlashMessages.MSG_INFO, "Your logout was successful.");
        }
        return "home/index";
    }

}
