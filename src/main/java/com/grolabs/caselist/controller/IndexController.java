package com.grolabs.caselist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grolabs.caselist.dto.JoinDto;
import com.grolabs.caselist.dto.Logout.LogoutDto;
import com.grolabs.caselist.dto.PasswordEditDto;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.entity.enums.UserStatus;
import com.grolabs.caselist.repository.UserRepository;
import com.grolabs.caselist.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
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
    @Operation(
            summary = "중복 확인",
            description = "사용자 이름이나 email의 중복을 확인한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "404", description = "value의 값이 null일때")
            }
    )
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
    @Operation(
            summary = "회원 가입",
            description = "회원 가입",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "463", description = "아이디 및 이메일 중복")
            }
    )
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
    @Operation(
            summary = "비밀번호 재설정",
            description = "비밀번호 재설정, 이메일을 전송해준다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공")
            }
    )
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
    @Operation(
            summary = "비밀번호 변경",
            description = "비밀번호 변경",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않을 때"),
                    @ApiResponse(responseCode = "464", description = "토큰이 맞지 않거나, 기존 비밀번호 틀렸을 때")
            }
    )
    @PutMapping("/password/update")
    public ResponseEntity<Void> updatePassword(@RequestParam String token, PasswordEditDto passwordEditDto) {
        if(joinService.updatePassword(token, passwordEditDto)) {
            return ResponseEntity.ok().build();
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * admin join api
     *
     * @param joinDto A DTO containing the following fields:
     *                - username
     *                - email
     *                - phoneNum
     *                - site
     * @return ResponseEntity<Void>
     */
    @Operation(
            summary = "관리자 계정 생성",
            description = "관리자 계정 생성",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "404", description = "이메일이나 아이디가 중복됐을 때")
            }
    )
    @PostMapping("/adminjoin")
    public ResponseEntity<Void> joinAdmin(JoinDto joinDto) {
        joinService.managerJoin(joinDto);
        return ResponseEntity.ok().build();
    }

    /**
     *
     * @param logoutDto A DTO containing the following fields:
     *                  - refreshToken
     * @return ResponseEntity<Void>
     */
    @Operation(
            summary = "관리자 계정 생성",
            description = "관리자 계정 생성",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "464", description = "refresh 토큰이 만료됐거나 잘못됐을 때")
            }
    )
    @PostMapping(value = "/clogout", consumes = {"text/plain", "text/plain;charset=UTF-8"})
    public ResponseEntity<Void> closeLogout(HttpServletRequest request){

        StringBuilder data = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            // StringBuilder를 String으로 변환 후 "Received data: " 제거
            String rawData = data.toString();
            if (rawData.startsWith("Received data: ")) {
                rawData = rawData.substring("Received data: ".length());
            }

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = objectMapper.readValue(rawData, Map.class);

            // refreshToken 추출
            String refreshToken = (String) map.get("refreshToken");
            System.out.println("Extracted refreshToken: " + refreshToken);

            // 여기에서 refreshToken을 사용해 로직 처리 가능
            // 예: joinService.closeLogout(refreshToken);

            return joinService.closeLogout(refreshToken);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).build(); // 잘못된 요청 반환
        }

    }

}
