package com.grolabs.caselist.controller;

import com.grolabs.caselist.dto.JoinDto;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.repository.UserRepository;
import com.grolabs.caselist.service.JoinService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class IndexController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


//    @GetMapping("/loginok")
//    public ResponseEntity<String> login() {
////        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////        String name = authentication.getName();
////        String authorities = authentication.getAuthorities().toString();
////
////
////        System.out.println("로그인한 유저 id:" + name);
////        System.out.println("유저 권한:" + authentication.getAuthorities());
////
////        Map<String, String> userInfo = new HashMap<>();
////        userInfo.put("username", name);
////        userInfo.put("authorities", authorities);
//
//        return ResponseEntity.ok("loginSuccess");
//    }


    @PostMapping("/join")
    public ResponseEntity<Void> join(JoinDto joinDto) {
        System.out.println(joinDto);

        boolean emailCheck = userRepository.existsByEmail(joinDto.getEmail());
        boolean nameCheck = userRepository.existsByUsername(joinDto.getUsername());

        if (emailCheck || nameCheck){
            throw new IllegalArgumentException("중복된 아이디 및 이메일이 존재합니다.");
        }

        User user = new User();
        user.setUsername(joinDto.getUsername());
        user.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        user.setEmail(joinDto.getEmail());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
//
//    @GetMapping("/joinForm")
//    public String joinForm() {
//        return "joinForm";
//    }

}
