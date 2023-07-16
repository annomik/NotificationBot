package com.example.notificationbot.controller;

import com.example.notificationbot.model.Task;
import com.example.notificationbot.model.User;
import com.example.notificationbot.model.UserRole;
import com.example.notificationbot.service.TaskService;
import com.example.notificationbot.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @GetMapping()
    public String index(Model model, @Param("keyword") String keyword, HttpServletRequest request) {
        List<Task> tasks = taskService.search(keyword);
        if (!request.isUserInRole(UserRole.ADMIN.getAuthority())) {
            model.addAttribute("tasks", taskService.search(keyword, getUserID(request.getUserPrincipal())));      // taskService.findAllByUserId(getUserID(request.getUserPrincipal())));
        } else {
            model.addAttribute("tasks", tasks);

        }
        model.addAttribute("keyword", keyword);

        return "tasks/tasks";
    }

    @GetMapping("/new")
    public String create(@ModelAttribute("task") Task task, Model model, Authentication authentication) {
        model.addAttribute("user", userService.findByEmail(authentication.getName()));
        return "tasks/new";
    }

    @PostMapping()
    public String update(@ModelAttribute("task") Task task, HttpServletRequest request) {
        Long userID = getUserID(request.getUserPrincipal());
        Optional<User> userById = userService.findById(userID);
        task.setUser(userById.orElseThrow(EntityNotFoundException::new));
        taskService.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") long id, Model model, Authentication authentication) {
        try {
            model.addAttribute("user", userService.findByEmail(authentication.getName()));
            model.addAttribute("task", taskService.findById(id).orElseThrow(EntityNotFoundException::new)); // change for something custom
        } catch (EntityNotFoundException e) {
            return "redirect:/tasks";
        }
        return "tasks/edit";
    }

    @PatchMapping("{id}")
    public String update(@ModelAttribute("task") Task task,
                         @PathVariable("id") long id, HttpServletRequest request) {
        Long userID = getUserID(request.getUserPrincipal());
        Optional<User> userById = userService.findById(userID);
        task.setUser(userById.orElseThrow(EntityNotFoundException::new));
        taskService.update(id, task);
        return "redirect:/tasks";
    }

    @DeleteMapping("{id}")
    public String delete(@PathVariable("id") long id) {
        taskService.delete(id);
        return "redirect:/tasks";
    }

    private Long getUserID(Principal principal) {
        return userService.findByEmail(principal.getName()).get().getId();
    }
}




























