package com.shinesoft.attendance.web;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shinesoft.attendance.domain.User;
import com.shinesoft.attendance.exception.BusinessException;
import com.shinesoft.attendance.service.UserService;
import com.shinesoft.attendance.web.form.UserForm;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model,
                       @ModelAttribute("error") String error,
                       @ModelAttribute("message") String message) {
        model.addAttribute("users", userService.list());
        model.addAttribute("error", error);
        model.addAttribute("message", message);
        return "users";
    }

    @GetMapping("/new")
    public String newForm(@ModelAttribute("userForm") UserForm form, Model model) {
        model.addAttribute("mode", "create");
        model.addAttribute("formAction", "/users");
        return "user-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("userForm") UserForm form,
                         BindingResult binding,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (binding.hasErrors() || form.getPassword() == null || form.getPassword().isBlank()) {
            if (form.getPassword() == null || form.getPassword().isBlank()) {
                binding.rejectValue("password", "required", "パスワードは必須です");
            }
            model.addAttribute("mode", "create");
            model.addAttribute("formAction", "/users");
            return "user-form";
        }
        try {
            userService.create(form.getUsername(), form.getPassword(), form.getRole());
            redirectAttributes.addFlashAttribute("message", "ユーザーを作成しました");
            return "redirect:/users";
        } catch (BusinessException ex) {
            binding.reject("business", ex.getMessage());
            model.addAttribute("mode", "create");
            model.addAttribute("formAction", "/users");
            return "user-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id,
                           @ModelAttribute("userForm") UserForm form,
                           Model model) {
        User user = userService.get(id);
        form.setUsername(user.getUsername());
        form.setRole(user.getRole());
        model.addAttribute("mode", "edit");
        model.addAttribute("userId", id);
        model.addAttribute("formAction", "/users/" + id);
        return "user-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("userForm") UserForm form,
                         BindingResult binding,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("mode", "edit");
            model.addAttribute("userId", id);
            model.addAttribute("formAction", "/users/" + id);
            return "user-form";
        }
        try {
            userService.update(id, form.getUsername(), form.getPassword(), form.getRole());
            redirectAttributes.addFlashAttribute("message", "ユーザーを更新しました");
            return "redirect:/users";
        } catch (BusinessException ex) {
            binding.reject("business", ex.getMessage());
            model.addAttribute("mode", "edit");
            model.addAttribute("userId", id);
            model.addAttribute("formAction", "/users/" + id);
            return "user-form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        userService.delete(id);
        redirectAttributes.addFlashAttribute("message", "ユーザーを削除しました");
        return "redirect:/users";
    }
}
