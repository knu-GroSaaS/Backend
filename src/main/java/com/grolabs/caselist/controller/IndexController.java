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


    @PostMapping("/join/dupli")
    public ResponseEntity<Boolean> checkDuplicate(
            @RequestParam String type,//username or email
            @RequestParam String value//
    ) {
        if (value.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(false);
        }

        boolean exists;
        if ("username".equals(type)) {
            exists = userRepository.existsByUsername(value);
        } else if ("email".equals(type)) {
            exists = userRepository.existsByEmail(value);
        } else {
            return ResponseEntity.badRequest().body(false);
        }

        // 중복이 없을 경우 true, 중복이 있을 경우 false 반환
        return ResponseEntity.ok(!exists);
    }


    @PostMapping("/join")
    public ResponseEntity<Void> join(JoinDto joinDto) {
        System.out.println(joinDto);
        User user = new User();
        user.setUsername(joinDto.getUsername());
        user.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        user.setEmail(joinDto.getEmail());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

}
