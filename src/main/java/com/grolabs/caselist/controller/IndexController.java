package com.grolabs.caselist.controller;

import com.grolabs.caselist.dto.JoinDto;
import com.grolabs.caselist.dto.PasswordEditDto;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.entity.enums.UserStatus;
import com.grolabs.caselist.repository.UserRepository;
import com.grolabs.caselist.service.JoinService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

    /**
     * duplicate check api
     * @param type type of value
     * @param value value
     * @return ResponseEntity<Boolean> A response containing a success message
     * **/
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

    /**
     * join api
     *
     * @param joinDto A DTO containing the following fields:
     *                - username
     *                - email
     *                - phoneNum
     *                - site
     * @return ResponseEntity<Void>
     */
    @PostMapping("/join")
    public ResponseEntity<Void> join(JoinDto joinDto) throws CloneNotSupportedException {
        joinService.joinUser(joinDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Password Request api
     *
     * Processes the user's request to change their password or perform related actions.
     *
     * @param accessToken The JWT token used for user authentication (provided in the Authorization header)
     * @return String
     *     - A message indicating whether the password request was successful or failed
     */
    @PostMapping("/password")
    public String requestPassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken){
        return joinService.requestPassword(accessToken);
    }

    /**
     *update password api
     *
     * @param token email
     * @param passwordEditDto A DTO containing the following fields:
     *                        - username
     *                        - currentPassword
     *                        - newPassword
     * @return ResponseEntity<Void>
     *     - success updatePassword -> ok code
     *     - fail updatePassword -> bad code
     */
    @PostMapping("/password/update")
    public ResponseEntity<Void> updatePassword(@RequestParam String token, PasswordEditDto passwordEditDto) {
        if(joinService.updatePassword(token, passwordEditDto)) {
            return ResponseEntity.ok().build();
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/adminjoin")
    public ResponseEntity<Void> joinAdmin(JoinDto joinDto) {
        joinService.managerJoin(joinDto);
        return ResponseEntity.ok().build();
    }

}
