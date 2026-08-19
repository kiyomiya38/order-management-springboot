package com.shinesoft.attendance.web; // Web（Controller）層のパッケージ

import java.security.Principal; // ログイン中ユーザー名を取得するために使う
import java.time.LocalDate; // 今日の日付表示に使う

import org.springframework.stereotype.Controller; // Controllerとして登録
import org.springframework.ui.Model; // 画面へ値を渡す
import org.springframework.web.bind.annotation.GetMapping; // GETマッピング
import org.springframework.web.bind.annotation.ModelAttribute; // フラッシュ属性の受け取りに使う
import org.springframework.web.bind.annotation.PostMapping; // POSTマッピング
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // リダイレクト時メッセージ

import com.shinesoft.attendance.domain.Attendance; // 今日の勤怠データ
import com.shinesoft.attendance.domain.AttendanceStatus; // 勤怠状態
import com.shinesoft.attendance.domain.User; // ログイン中のアプリ利用者
import com.shinesoft.attendance.exception.BusinessException; // 業務例外
import com.shinesoft.attendance.service.AttendanceService; // 勤怠業務
import com.shinesoft.attendance.service.UserService; // ユーザー業務

@Controller // 画面制御クラス
public class HomeController {
    private final AttendanceService service; // 勤怠処理
    private final UserService userService; // ユーザー処理

    public HomeController(AttendanceService service, UserService userService) {
        this.service = service; // 依存注入
        this.userService = userService; // 依存注入
    }

    @GetMapping("/") // トップ画面
    public String index(Model model,
                        @ModelAttribute("error") String error,
                        @ModelAttribute("message") String message,
                        Principal principal) {
        User user = userService.getByUsername(principal.getName()); // 戻り値の型を明示してログイン中ユーザーを取得
        Attendance today = service.getTodayAttendance(user.getId()); // 当日の勤怠データ
        AttendanceStatus status = today == null ? AttendanceStatus.NOT_STARTED : today.getStatus(); // 状態決定

        model.addAttribute("workDate", LocalDate.now()); // 日付
        model.addAttribute("username", user.getUsername()); // 画面表示用ユーザー名
        model.addAttribute("isAdmin", "ROLE_ADMIN".equals(user.getRole())); // 管理者判定（メニュー表示に使用）
        model.addAttribute("status", status); // 状態本体
        model.addAttribute("statusLabel", statusLabel(status)); // 状態表示文字
        model.addAttribute("statusClass", statusClass(status)); // 状態バッジCSS
        model.addAttribute("startTime", today != null ? today.getStartTime() : null); // 出勤時刻
        model.addAttribute("endTime", today != null ? today.getEndTime() : null); // 退勤時刻
        model.addAttribute("error", error); // 失敗メッセージ
        model.addAttribute("message", message); // 成功メッセージ

        return "index"; // templates/index.html
    }

    @PostMapping("/clock-in") // 出勤
    public String clockIn(RedirectAttributes redirectAttributes, Principal principal) {
        User user = userService.getByUsername(principal.getName()); // ログイン中ユーザー
        try {
            service.clockIn(user.getId()); // 出勤処理
            redirectAttributes.addFlashAttribute("message", "出勤しました");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage()); // 業務エラー表示
        }
        return "redirect:/"; // トップへ戻る
    }

    @PostMapping("/clock-out") // 退勤
    public String clockOut(RedirectAttributes redirectAttributes, Principal principal) {
        User user = userService.getByUsername(principal.getName()); // ログイン中ユーザー
        try {
            service.clockOut(user.getId()); // 退勤処理
            redirectAttributes.addFlashAttribute("message", "退勤しました");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage()); // 業務エラー表示
        }
        return "redirect:/"; // トップへ戻る
    }

    private String statusClass(AttendanceStatus status) { // 状態に応じたCSSクラスを返す
        // 短縮コースで学習済みのif文を使って状態ごとに返り値を切り替える
        if (status == AttendanceStatus.WORKING) {
            return "status-badge status-working";
        }
        if (status == AttendanceStatus.FINISHED) {
            return "status-badge status-finished";
        }
        return "status-badge";
    }

    private String statusLabel(AttendanceStatus status) { // 状態に応じた表示ラベル
        if (status == AttendanceStatus.WORKING) {
            return "出勤中";
        }
        if (status == AttendanceStatus.FINISHED) {
            return "退勤済み";
        }
        return "未出勤";
    }
}