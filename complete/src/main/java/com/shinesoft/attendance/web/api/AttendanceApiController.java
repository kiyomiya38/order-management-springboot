package com.shinesoft.attendance.web.api;

import java.security.Principal;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shinesoft.attendance.service.AttendanceService;
import com.shinesoft.attendance.service.UserService;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceApiController {
    private final AttendanceService attendanceService;
    private final UserService userService;

    public AttendanceApiController(AttendanceService attendanceService, UserService userService) {
        this.attendanceService = attendanceService;
        this.userService = userService;
    }

    @PostMapping("/clock-in")
    public Map<String, String> clockIn(Principal principal) {
        var user = userService.getByUsername(principal.getName());
        attendanceService.clockIn(user.getId());
        return Map.of("message", "出勤しました");
    }

    @PostMapping("/clock-out")
    public Map<String, String> clockOut(Principal principal) {
        var user = userService.getByUsername(principal.getName());
        attendanceService.clockOut(user.getId());
        return Map.of("message", "退勤しました");
    }
}
