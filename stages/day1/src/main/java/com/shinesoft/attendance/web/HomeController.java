package com.shinesoft.attendance.web;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("workDate", LocalDate.now());
        model.addAttribute("statusLabel", "未出勤");
        model.addAttribute("startTime", null);
        model.addAttribute("endTime", null);
        return "index";
    }
}