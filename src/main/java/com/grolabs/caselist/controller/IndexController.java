package com.grolabs.caselist.controller;

import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class IndexController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/loginForm")
    public String loginForm(@ModelAttribute("user") User user, HttpSession session) {
        if (session.getAttribute("SPRING_SECURITY_CONTEXT") != null) {
            return "redirect:/";
        }//login 되어있을시 "/"로 redirect
        return "loginForm";
    }


    @PostMapping("/join")
    public String join(User user) {
        System.out.println(user);
        user.setUsertype("ROLE_USER");
        String rawPassword = user.getPassword();
        String enPassword=passwordEncoder.encode(rawPassword);
        user.setPassword(enPassword);
        userRepository.save(user); //회원가입 잘됨 .but 비밀번호: 1234=> 시큐리티로 로그인을 할 수 없음. 이유는 패스워드가 암호화가 안되었기 때문
        return "redirect:/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }


}
