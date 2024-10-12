package com.grolabs.caselist.controller;

import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.repository.UserRepository;
import com.grolabs.caselist.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class IndexController {

    private final LoginService loginService;


    @PostMapping("/loginok")
    public ResponseEntity<Map<String,String>> loginForm(@ModelAttribute("user") User user, HttpSession session) {
//        if (session.getAttribute("SPRING_SECURITY_CONTEXT") != null) {
//            return "redirect:/";
//        }//login 되어있을시 "/"로 redirect
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        String authorities = authentication.getAuthorities().toString();


        System.out.println("로그인한 유저 id:" + name);
        System.out.println("유저 권한:" + authentication.getAuthorities());

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", name);
        userInfo.put("authorities", authorities);

        return ResponseEntity.ok(userInfo);
    }


    @PostMapping("/join")
    public ResponseEntity<Void> join(User user) {
        loginService.joinUser(user);
        return ResponseEntity.ok().build();
    }
//
//    @GetMapping("/joinForm")
//    public String joinForm() {
//        return "joinForm";
//    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();

        return ResponseEntity.ok().build();
    }

}
