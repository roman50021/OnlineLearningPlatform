package com.example.onlinelearningplatform.controllers;

import com.example.onlinelearningplatform.dto.LoginUserDto;
import com.example.onlinelearningplatform.dto.UserDto;
import com.example.onlinelearningplatform.models.User;
import com.example.onlinelearningplatform.service.implementations.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserServiceImpl userService;

    @GetMapping(value = "/")
    public String mainPage(Model model) {
        return "main";
    }

    @GetMapping(value = "/login")
    public String loginForm(Model model) {
        LoginUserDto loginUserDto = new LoginUserDto();
        model.addAttribute("loginUserDto", loginUserDto);
        return "login";
    }

    @PostMapping(value = "/login")
    public String login(@ModelAttribute("loginUserDto") LoginUserDto loginUserDto,
                        RedirectAttributes redirectAttributes) {
        String email = loginUserDto.getEmail();
        String password = loginUserDto.getPassword();

        if (userService.authenticateByEmail(email, password)) {
            // Успешная авторизация
            redirectAttributes.addFlashAttribute("message", "Login successful");
            System.out.println("Успешная авторизация : " + email);
            return "redirect:/home";
        } else {
            // Неправильный Email или пароль, или пользователь отключен или заблокирован
            redirectAttributes.addFlashAttribute("error", "Invalid Email and Password");
            System.out.println("Авторизация : Неправильный Email или пароль, или пользователь отключен или заблокирован" + email);
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

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
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        // Перенаправляем на страницу входа
        return "redirect:/main";
    }

}

