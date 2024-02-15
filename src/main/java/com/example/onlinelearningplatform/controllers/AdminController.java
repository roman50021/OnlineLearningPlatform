package com.example.onlinelearningplatform.controllers;

import com.example.onlinelearningplatform.models.User;
import com.example.onlinelearningplatform.service.implementations.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserServiceImpl userService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String listRegisteredUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

}
