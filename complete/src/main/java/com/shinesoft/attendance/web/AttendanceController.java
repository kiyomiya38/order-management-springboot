package com.shinesoft.attendance.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shinesoft.attendance.service.AttendanceService;
import com.shinesoft.attendance.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/attendances")
public class AttendanceController {
    private final AttendanceService service;
    private final UserService userService;

    public AttendanceController(AttendanceService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model, Principal principal) {
        var user = userService.getByUsername(principal.getName());
        model.addAttribute("attendances", service.listAttendances(user.getId()));
        model.addAttribute("username", user.getUsername());
        return "attendances";
    }
}
