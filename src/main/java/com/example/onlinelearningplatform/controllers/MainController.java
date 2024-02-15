package com.example.onlinelearningplatform.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class MainController {

    @GetMapping(value = "/")
    public String mainPage(Model model) {
        return "main";
    }

    @GetMapping("/main")
    public String forMain(){
        return "main";
    }

    @GetMapping("/home")
    public String home(){ //secured
        return "home";
    }

    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }

    @GetMapping("/info")
    public String userDate(Principal principal){
        return principal.getName();
    }
}
