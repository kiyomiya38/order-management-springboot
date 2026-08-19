package com.shinesoft.attendance.web;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shinesoft.attendance.domain.Attendance;
import com.shinesoft.attendance.exception.BusinessException;
import com.shinesoft.attendance.service.AttendanceService;
import com.shinesoft.attendance.service.UserService;
import com.shinesoft.attendance.web.form.AdminAttendanceForm;

@Controller
@RequestMapping("/admin/attendances")
public class AdminAttendanceController {
    private final AttendanceService attendanceService;
    private final UserService userService;

    public AdminAttendanceController(AttendanceService attendanceService, UserService userService) {
        this.attendanceService = attendanceService;
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model,
                       @ModelAttribute("error") String error,
                       @ModelAttribute("message") String message) {
        List<Attendance> attendances = attendanceService.listAllAttendances();
        model.addAttribute("attendances", attendances);
        model.addAttribute("error", error);
        model.addAttribute("message", message);
        return "admin-attendances";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id,
                       @ModelAttribute("form") AdminAttendanceForm form,
                       Model model) {
        Attendance attendance = attendanceService.getAttendance(id);
        form.setUserId(attendance.getUser().getId());
        form.setUsername(attendance.getUser().getUsername());
        form.setWorkDate(attendance.getWorkDate());
        form.setStartTime(attendance.getStartTime());
        form.setEndTime(attendance.getEndTime());
        form.setStatus(attendance.getStatus());

        model.addAttribute("attendanceId", id);
        return "admin-attendance-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("form") AdminAttendanceForm form,
                         BindingResult binding,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (binding.hasErrors()) {
            if (form.getUserId() != null) {
                form.setUsername(userService.get(form.getUserId()).getUsername());
            }
            model.addAttribute("attendanceId", id);
            return "admin-attendance-form";
        }

        try {
            attendanceService.updateAttendance(id,
                form.getUserId(),
                form.getWorkDate(),
                form.getStartTime(),
                form.getEndTime(),
                form.getStatus());
            redirectAttributes.addFlashAttribute("message", "勤怠を更新しました");
            return "redirect:/admin/attendances";
        } catch (BusinessException ex) {
            binding.reject("business", ex.getMessage());
            if (form.getUserId() != null) {
                form.setUsername(userService.get(form.getUserId()).getUsername());
            }
            model.addAttribute("attendanceId", id);
            return "admin-attendance-form";
        }
    }
}
