package com.example.onlinelearningplatform.controllers;

import com.example.onlinelearningplatform.dto.LoginUserDto;
import com.example.onlinelearningplatform.dto.UserDto;
import com.example.onlinelearningplatform.models.User;
import com.example.onlinelearningplatform.service.UserDetailsServiceImp;
import com.example.onlinelearningplatform.service.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller

public class AuthController {

    private UserService userService;

    @Autowired
    public UserDetailsServiceImp userDetailsServiceImp;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("login")
    public String loginForm(Model model) {
        LoginUserDto loginUserDto = new LoginUserDto();
        model.addAttribute("loginUserDto", loginUserDto);
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginUserDto") LoginUserDto loginUserDto,
                        RedirectAttributes redirectAttributes) {
        String email = loginUserDto.getEmail();
        String password = loginUserDto.getPassword();

        if (userDetailsServiceImp.authenticateByEmail(email, password)) {
            // Успешная авторизация
            redirectAttributes.addFlashAttribute("message", "Login successful");
            return "redirect:/home";
        } else {
            // Неправильный Email или пароль
            redirectAttributes.addFlashAttribute("error", "Invalid Email and Password");
            return "redirect:/login";
        }

    }

    // handler method to handle user registration request
    @GetMapping("register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model) {
        System.out.println("Received UserDto: " + user);

        if (result.hasErrors()) {
            // Если есть ошибки валидации
            System.out.println("Validation errors: " + result.getAllErrors());
            model.addAttribute("user", user);
            return "register";
        }

        User existing = userService.findByEmail(user.getEmail());

        if (existing != null) {
            // Если пользователь с таким email уже существует
            result.rejectValue("email", null, "There is already an account registered with that email");
            model.addAttribute("user", user);
            return "register";
        }

        userService.save(user);
        System.out.println("User saved successfully");

        return "redirect:/register?success";
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String listRegisteredUsers(Model model) {
        List<User> users = userService.getAllUsers();  // Change this to fetch User entities
        model.addAttribute("users", users);
        return "users";
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Завершаем текущий сеанс
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        // Перенаправляем на страницу входа
        return "redirect:/login";
    }
}

