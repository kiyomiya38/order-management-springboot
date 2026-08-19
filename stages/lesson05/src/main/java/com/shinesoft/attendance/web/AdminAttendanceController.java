package com.shinesoft.attendance.web; // 管理者向け画面制御を置くパッケージ

import java.util.List; // 一覧表示で使用

import jakarta.validation.Valid; // 入力バリデーションを有効化

import org.springframework.stereotype.Controller; // Controller登録
import org.springframework.ui.Model; // 画面へ値を渡す
import org.springframework.validation.BindingResult; // バリデーション結果を受ける
import org.springframework.web.bind.annotation.*; // Mapping系アノテーション
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // リダイレクト時メッセージ

import com.shinesoft.attendance.domain.Attendance; // 勤怠エンティティ
import com.shinesoft.attendance.exception.BusinessException; // 業務例外
import com.shinesoft.attendance.service.AttendanceService; // 勤怠業務ロジック
import com.shinesoft.attendance.service.UserService; // ユーザー情報取得に使う
import com.shinesoft.attendance.web.form.AdminAttendanceForm; // 管理者編集フォーム

@Controller // Spring MVC Controller
@RequestMapping("/admin/attendances") // 管理者勤怠URLを担当
public class AdminAttendanceController {
    private final AttendanceService attendanceService; // 勤怠処理
    private final UserService userService; // ユーザー処理

    public AdminAttendanceController(AttendanceService attendanceService, UserService userService) {
        this.attendanceService = attendanceService; // 依存注入
        this.userService = userService; // 依存注入
    }

    @GetMapping // GET /admin/attendances（一覧）
    public String list(Model model,
                       @ModelAttribute("error") String error,
                       @ModelAttribute("message") String message) {
        List<Attendance> attendances = attendanceService.listAllAttendances(); // 全ユーザー勤怠を取得
        model.addAttribute("attendances", attendances); // 画面へ一覧を渡す
        model.addAttribute("error", error); // エラー表示
        model.addAttribute("message", message); // 成功表示
        return "admin-attendances"; // templates/admin-attendances.html
    }

    @GetMapping("/{id}/edit") // GET /admin/attendances/{id}/edit（編集画面）
    public String edit(@PathVariable("id") Long id,
                       @ModelAttribute("form") AdminAttendanceForm form,
                       Model model) {
        Attendance attendance = attendanceService.getAttendance(id); // 編集対象を取得
        form.setUserId(attendance.getUser().getId()); // userIdをフォームへ
        form.setUsername(attendance.getUser().getUsername()); // 表示用ユーザー名
        form.setWorkDate(attendance.getWorkDate()); // 勤務日
        form.setStartTime(attendance.getStartTime()); // 出勤時刻
        form.setEndTime(attendance.getEndTime()); // 退勤時刻
        form.setStatus(attendance.getStatus()); // 状態

        model.addAttribute("attendanceId", id); // フォーム送信先組み立てに使う
        return "admin-attendance-form"; // templates/admin-attendance-form.html
    }

    @PostMapping("/{id}") // POST /admin/attendances/{id}（更新実行）
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("form") AdminAttendanceForm form,
                         BindingResult binding,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (binding.hasErrors()) { // 入力チェックエラー
            if (form.getUserId() != null) {
                form.setUsername(userService.get(form.getUserId()).getUsername()); // 再表示用ユーザー名を復元
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
                form.getStatus()); // 業務更新処理
            redirectAttributes.addFlashAttribute("message", "勤怠を更新しました");
            return "redirect:/admin/attendances";
        } catch (BusinessException ex) {
            binding.reject("business", ex.getMessage()); // 業務ルール違反
            if (form.getUserId() != null) {
                form.setUsername(userService.get(form.getUserId()).getUsername()); // 再表示時に復元
            }
            model.addAttribute("attendanceId", id);
            return "admin-attendance-form";
        }
    }
}