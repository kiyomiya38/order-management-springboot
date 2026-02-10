package com.shinesoft.attendance.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shinesoft.attendance.service.AttendanceService;

@Controller
@RequestMapping("/attendances")
public class AttendanceController {
    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("attendances", service.listAttendances(1L));
        model.addAttribute("username", "user1");
        return "attendances";
    }
}
