// 画面（Web）層のクラスを置くパッケージ
package com.shinesoft.attendance.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shinesoft.attendance.domain.Attendance;
import com.shinesoft.attendance.domain.AttendanceStatus;
import com.shinesoft.attendance.exception.BusinessException;
import com.shinesoft.attendance.service.AttendanceService;

// 画面表示を担当するController
@Controller
public class HomeController {
    // Lesson3は固定ユーザーで進める（認証はLesson5で実装）
    private static final Long TRAINING_USER_ID = 1L;
    // 日時表示フォーマット
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 業務ロジックはServiceに委譲
    private final AttendanceService attendanceService;

    public HomeController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // トップ画面表示
    @GetMapping("/")
    public String index(Model model,
                        // リダイレクト時に受け取る成功メッセージ（任意）
                        @RequestParam(value = "message", required = false) String message,
                        // リダイレクト時に受け取るエラーメッセージ（任意）
                        @RequestParam(value = "error", required = false) String error) {
        // 当日の勤怠を取得
        Optional<Attendance> today = attendanceService.findToday(TRAINING_USER_ID);

        // テンプレートに渡す表示データを準備
        model.addAttribute("workDate", LocalDate.now());
        model.addAttribute("statusLabel", toStatusLabel(today));
        model.addAttribute("startTime", format(today.map(Attendance::getStartTime).orElse(null)));
        model.addAttribute("endTime", format(today.map(Attendance::getEndTime).orElse(null)));
        // 出勤ボタン: 当日レコードがまだ無い時のみ表示
        model.addAttribute("canClockIn", today.isEmpty());
        // 退勤ボタン: 当日レコードがあり、状態がWORKINGの時のみ表示
        model.addAttribute("canClockOut", today.isPresent() && today.get().getStatus() == AttendanceStatus.WORKING);
        model.addAttribute("message", message);
        model.addAttribute("error", error);
        // templates/index.html を表示
        return "index";
    }

    // 出勤ボタン押下時の処理
    @PostMapping("/clock-in")
    public String clockIn(RedirectAttributes redirectAttributes) {
        try {
            // 出勤処理を実行
            attendanceService.clockIn(TRAINING_USER_ID);
            redirectAttributes.addAttribute("message", "出勤を記録しました");
        } catch (BusinessException e) {
            // 業務エラーは画面向けメッセージとして返す
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        // POST後はGETへリダイレクト（PRGパターン）
        return "redirect:/";
    }

    // 退勤ボタン押下時の処理（Lesson3で追加）
    @PostMapping("/clock-out")
    public String clockOut(RedirectAttributes redirectAttributes) {
        try {
            // 退勤処理を実行
            attendanceService.clockOut(TRAINING_USER_ID);
            redirectAttributes.addAttribute("message", "退勤を記録しました");
        } catch (BusinessException e) {
            // 業務エラーは画面向けメッセージとして返す
            redirectAttributes.addAttribute("error", e.getMessage());
        }
        // POST後はGETへリダイレクト（PRGパターン）
        return "redirect:/";
    }

    // 勤怠状態を画面表示用文字列へ変換
    private String toStatusLabel(Optional<Attendance> today) {
        if (today.isEmpty()) {
            return "未出勤";
        }
        AttendanceStatus status = today.get().getStatus();
        if (status == AttendanceStatus.WORKING) {
            return "出勤中";
        }
        if (status == AttendanceStatus.FINISHED) {
            return "退勤済み";
        }
        return "未出勤";
    }

    // 日時表示の共通フォーマッタ
    private String format(LocalDateTime value) {
        if (value == null) {
            // 値が無い時は "-" を表示
            return "-";
        }
        return value.format(FMT);
    }
}