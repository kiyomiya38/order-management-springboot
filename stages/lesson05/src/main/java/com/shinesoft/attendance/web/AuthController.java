package com.shinesoft.attendance.web; // 画面制御（Web）層のパッケージ

import org.springframework.stereotype.Controller; // このクラスをControllerとして登録
import org.springframework.ui.Model; // 画面へ値を渡すための箱
import org.springframework.web.bind.annotation.GetMapping; // GETリクエストを受ける
import org.springframework.web.bind.annotation.RequestParam; // クエリパラメータを受ける

@Controller // Spring MVCのControllerクラス
public class AuthController {
    @GetMapping("/login") // /login へGETアクセスされたときにこのメソッドを実行
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) { // 認証失敗時（?error が付く）
            model.addAttribute("error", "ユーザー名またはパスワードが正しくありません"); // 画面表示用エラー文言
        }
        if (logout != null) { // ログアウト直後（?logout が付く）
            model.addAttribute("message", "ログアウトしました"); // 画面表示用メッセージ
        }
        return "login"; // templates/login.html を表示
    }
}