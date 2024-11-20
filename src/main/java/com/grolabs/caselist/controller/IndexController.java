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

    private final JoinService joinService;


    @PostMapping("/join/dupli")
    public ResponseEntity<Boolean> checkDuplicate(
            @RequestParam String type,//username or email
            @RequestParam String value//
    ) {
        if (value.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(false);
        }
        // 중복이 없을 경우 true, 중복이 있을 경우 false 반환
        return ResponseEntity.ok(joinService.checkDuplication(type, value));
    }


    @PostMapping("/join")
    public ResponseEntity<Void> join(JoinDto joinDto) {
        joinService.joinUser(joinDto);
        return ResponseEntity.ok().build();
    }

}
