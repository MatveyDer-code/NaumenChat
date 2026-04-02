package ru.matveyder.NauJava.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.matveyder.NauJava.service.UserService;

/**
 * Контроллер для регистрации пользователей.
 */
@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/registration")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String email,
                          Model model) {
        try {
            userService.registerUser(username, password, email);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", e.getMessage());
            return "registration";
        }
    }
}