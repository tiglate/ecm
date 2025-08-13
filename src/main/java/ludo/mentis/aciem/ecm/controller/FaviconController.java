package ludo.mentis.aciem.ecm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FaviconController {

    @GetMapping("/favicon.ico")
    public String favicon() {
        return "forward:/static/favicon.ico";
    }
}