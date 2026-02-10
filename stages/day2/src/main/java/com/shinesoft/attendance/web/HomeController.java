package com.shinesoft.attendance.web;

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

@Controller
public class HomeController {
    private static final Long USER_ID = 1L;
    private final AttendanceService service;

    public HomeController(AttendanceService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(Model model,
                        @ModelAttribute("error") String error,
                        @ModelAttribute("message") String message) {
        Attendance today = service.getTodayAttendance(USER_ID);
        AttendanceStatus status = today == null ? AttendanceStatus.NOT_STARTED : today.getStatus();

        model.addAttribute("workDate", LocalDate.now());
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
    public String clockIn(RedirectAttributes redirectAttributes) {
        try {
            service.clockIn(USER_ID);
            redirectAttributes.addFlashAttribute("message", "出勤しました");
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
