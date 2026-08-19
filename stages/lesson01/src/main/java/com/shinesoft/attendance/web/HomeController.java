package com.shinesoft.attendance.web; // `web` は画面/HTTPリクエストを扱う層

import java.time.LocalDate; // 今日の日付を扱うJava標準クラス

import org.springframework.stereotype.Controller; // 画面表示用Controllerを示す
import org.springframework.ui.Model; // Controllerからテンプレートへ値を渡す入れ物
import org.springframework.web.bind.annotation.GetMapping; // HTTP GETのURLとメソッドを対応付ける

@Controller // 「画面を返すController」としてSpringに登録
public class HomeController {

    @GetMapping("/") // ブラウザが "/" にGETアクセスした時に呼ばれる
    public String index(Model model) {
        model.addAttribute("workDate", LocalDate.now()); // "workDate" という名前で値を入れる。HTML側の ${workDate} と対応する
        model.addAttribute("statusLabel", "未出勤"); // "statusLabel" という名前で値を入れる。HTML側の ${statusLabel} と対応する
        model.addAttribute("startTime", "-"); // Lesson1では固定表示。HTML側の ${startTime} と対応する
        model.addAttribute("endTime", "-"); // Lesson1では固定表示。HTML側の ${endTime} と対応する
        return "index"; // templates/index.html を表示（先頭に "/" は付けない）
    }
}