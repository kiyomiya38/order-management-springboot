package com.shinesoft.attendance.web; // Web（Controller）層のパッケージ

import jakarta.validation.Valid; // 入力バリデーションを有効化

import org.springframework.stereotype.Controller; // Controllerとして登録
import org.springframework.ui.Model; // 画面へ値を渡す
import org.springframework.validation.BindingResult; // バリデーション結果を受ける
import org.springframework.web.bind.annotation.*; // Mapping系アノテーション
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // リダイレクト時のメッセージ保持

import com.shinesoft.attendance.domain.User; // 編集時に既存ユーザー情報を扱う
import com.shinesoft.attendance.exception.BusinessException; // 業務例外
import com.shinesoft.attendance.service.UserService; // 業務ロジック呼び出し先
import com.shinesoft.attendance.web.form.UserForm; // 画面入力フォーム

@Controller // 画面制御クラス
@RequestMapping("/users") // /users 配下を担当
public class UserController {
    private final UserService userService; // ユーザー管理業務を委譲

    public UserController(UserService userService) {
        this.userService = userService; // 依存注入
    }

    @GetMapping // GET /users（一覧画面）
    public String list(Model model,
                       @ModelAttribute("error") String error,
                       @ModelAttribute("message") String message) {
        model.addAttribute("users", userService.list()); // 一覧データ
        model.addAttribute("error", error); // 失敗メッセージ
        model.addAttribute("message", message); // 成功メッセージ
        return "users"; // templates/users.html
    }

    @GetMapping("/new") // GET /users/new（新規作成フォーム）
    public String newForm(@ModelAttribute("userForm") UserForm form, Model model) {
        model.addAttribute("mode", "create"); // 作成モード
        model.addAttribute("formAction", "/users"); // 送信先
        return "user-form"; // templates/user-form.html
    }

    @PostMapping // POST /users（新規作成実行）
    public String create(@Valid @ModelAttribute("userForm") UserForm form,
                         BindingResult binding,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (binding.hasErrors() || form.getPassword() == null || form.getPassword().isBlank()) { // 入力チェック
            if (form.getPassword() == null || form.getPassword().isBlank()) {
                binding.rejectValue("password", "required", "パスワードは必須です"); // 新規時はパスワード必須
            }
            model.addAttribute("mode", "create");
            model.addAttribute("formAction", "/users");
            return "user-form"; // 入力画面へ戻す
        }
        try {
            userService.create(form.getUsername(), form.getPassword(), form.getRole()); // 作成実行
            redirectAttributes.addFlashAttribute("message", "ユーザーを作成しました");
            return "redirect:/users"; // 一覧へ戻す
        } catch (BusinessException ex) {
            binding.reject("business", ex.getMessage()); // 業務エラー表示
            model.addAttribute("mode", "create");
            model.addAttribute("formAction", "/users");
            return "user-form";
        }
    }

    @GetMapping("/{id}/edit") // GET /users/{id}/edit（編集フォーム）
    public String editForm(@PathVariable("id") Long id,
                           @ModelAttribute("userForm") UserForm form,
                           Model model) {
        User user = userService.get(id); // 既存ユーザー取得
        form.setUsername(user.getUsername()); // 初期値セット
        form.setRole(user.getRole()); // 初期値セット
        model.addAttribute("mode", "edit"); // 編集モード
        model.addAttribute("userId", id); // 画面表示補助
        model.addAttribute("formAction", "/users/" + id); // 更新送信先
        return "user-form";
    }

    @PostMapping("/{id}") // POST /users/{id}（更新実行）
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("userForm") UserForm form,
                         BindingResult binding,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (binding.hasErrors()) { // 入力エラー時
            model.addAttribute("mode", "edit");
            model.addAttribute("userId", id);
            model.addAttribute("formAction", "/users/" + id);
            return "user-form";
        }
        try {
            userService.update(id, form.getUsername(), form.getPassword(), form.getRole()); // 更新実行
            redirectAttributes.addFlashAttribute("message", "ユーザーを更新しました");
            return "redirect:/users";
        } catch (BusinessException ex) {
            binding.reject("business", ex.getMessage()); // 業務エラー
            model.addAttribute("mode", "edit");
            model.addAttribute("userId", id);
            model.addAttribute("formAction", "/users/" + id);
            return "user-form";
        }
    }

    @PostMapping("/{id}/delete") // POST /users/{id}/delete（削除実行）
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id); // 削除実行
            redirectAttributes.addFlashAttribute("message", "ユーザーを削除しました");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/users";
    }
}