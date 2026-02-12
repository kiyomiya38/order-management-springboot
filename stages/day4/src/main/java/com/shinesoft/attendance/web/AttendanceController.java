package com.shinesoft.attendance.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shinesoft.attendance.domain.Attendance;
import com.shinesoft.attendance.service.AttendanceService;

@Controller
public class AttendanceController {
    private static final Long TRAINING_USER_ID = 1L;

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/attendances")
    public String list(Model model) {
        List<Attendance> rows = attendanceService.findAttendances(TRAINING_USER_ID);
        model.addAttribute("rows", rows);
        return "attendances";
    }
}