package com.epam.capstone.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginForm(
            @RequestParam(value = "error",  required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("errorMsg", "Неверное имя пользователя или пароль");
        }
        if (logout != null) {
            model.addAttribute("msg", "Вы успешно вышли из системы");
        }
        return "login";
    }
}
