package com.grolabs.caselist.controller;

import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class IndexController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/loginForm")
    public String loginForm(@ModelAttribute("user") User user, HttpSession session) {
        if (session.getAttribute("SPRING_SECURITY_CONTEXT") != null) {
            return "redirect:/";
        }//login 되어있을시 "/"로 redirect

//        boolean isUsernameTaken = userRepository.existsByUsername(user.getUsername());
//        boolean isEmailTaken = userRepository.existsByEmail(user.getEmail());
//
//        if (isEmailTaken || isUsernameTaken){
//            throw new IllegalArgumentException("이미 존재하는 이메일이나 아이디 입니다.");
//        }

        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }


}
