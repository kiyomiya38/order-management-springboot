package com.shinesoft.attendance.web;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shinesoft.attendance.domain.Attendance;
import com.shinesoft.attendance.domain.AttendanceStatus;
import com.shinesoft.attendance.exception.BusinessException;
import com.shinesoft.attendance.service.AttendanceService;
import com.shinesoft.attendance.service.UserService;

@Controller
public class HomeController {
    private final AttendanceService service;
    private final UserService userService;

    public HomeController(AttendanceService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(Model model,
                        @ModelAttribute("error") String error,
                        @ModelAttribute("message") String message,
                        Principal principal) {
        var user = userService.getByUsername(principal.getName());
        Attendance today = service.getTodayAttendance(user.getId());
        AttendanceStatus status = today == null ? AttendanceStatus.NOT_STARTED : today.getStatus();

        model.addAttribute("workDate", LocalDate.now());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("isAdmin", "ROLE_ADMIN".equals(user.getRole()));
        model.addAttribute("status", status);
        model.addAttribute("statusLabel", status.getLabel());
        model.addAttribute("statusClass", statusClass(status));
        model.addAttribute("startTime", today != null ? today.getStartTime() : null);
        model.addAttribute("endTime", today != null ? today.getEndTime() : null);
        model.addAttribute("error", error);
        model.addAttribute("message", message);

        return "index";
    }

    @PostMapping("/clock-in")
    public String clockIn(RedirectAttributes redirectAttributes, Principal principal) {
        var user = userService.getByUsername(principal.getName());
        try {
            service.clockIn(user.getId());
            redirectAttributes.addFlashAttribute("message", "出勤しました");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/clock-out")
    public String clockOut(RedirectAttributes redirectAttributes, Principal principal) {
        var user = userService.getByUsername(principal.getName());
        try {
            service.clockOut(user.getId());
            redirectAttributes.addFlashAttribute("message", "退勤しました");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/";
    }

    private String statusClass(AttendanceStatus status) {
        return switch (status) {
            case WORKING -> "status-badge status-working";
            case FINISHED -> "status-badge status-finished";
            default -> "status-badge";
        };
    }
}
