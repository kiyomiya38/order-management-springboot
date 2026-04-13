package com.shinesoft.sandbox.web;

import com.shinesoft.sandbox.service.GreetingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GreetingController {
    private final GreetingService greetingService;

    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(name = "name", required = false) String name, Model model) {
        model.addAttribute("message", greetingService.createMessage(name));
        return "hello";
    }
}
