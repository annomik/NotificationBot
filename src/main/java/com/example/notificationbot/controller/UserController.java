package com.example.notificationbot.controller;


import com.example.notificationbot.model.User;
import com.example.notificationbot.model.UserRole;
import com.example.notificationbot.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/users";
    }

    @GetMapping("/new")
    public String create(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("roles", List.of(UserRole.values()));
        return "users/new";
    }

    @PostMapping("/new")
    public String update(@ModelAttribute("user") User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return "redirect:/users";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUser(Integer.parseInt(code));

        return "redirect:/users/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "users/login";
    }


    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") long id, Model model) {
        try {
            model.addAttribute("user", userService.findById(id).orElseThrow(EntityNotFoundException::new)); // change for something custom
            model.addAttribute("roles", List.of(UserRole.values()));
        } catch (EntityNotFoundException e) {
            return "redirect:/users";
        }
        return "users/edit";
    }

    @PatchMapping("{id}")
    public String update(@ModelAttribute("user") User user,
                         @PathVariable("id") long id,
                         Model model) {

        model.addAttribute("roles", List.of(UserRole.values()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.update(id, user);
        return "redirect:/users";
    }

    @DeleteMapping("{id}")
    public String delete(@PathVariable("id") long id) {
        userService.delete(id);
        return "redirect:/users";
    }

}




























