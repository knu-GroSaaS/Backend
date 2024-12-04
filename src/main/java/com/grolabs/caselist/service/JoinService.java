package com.grolabs.caselist.service;


import com.grolabs.caselist.dto.JoinDto;
import com.grolabs.caselist.dto.Logout.LogoutDto;
import com.grolabs.caselist.dto.PasswordEditDto;
import com.grolabs.caselist.entity.LoginHistory;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.entity.enums.AuthStatus;
import com.grolabs.caselist.entity.enums.UserStatus;
import com.grolabs.caselist.jwt.JWTUtil;
import com.grolabs.caselist.repository.LoginHistoryRepository;
import com.grolabs.caselist.repository.RefreshEntityRepository;
import com.grolabs.caselist.repository.UserRepository;
import com.grolabs.caselist.service.email.EmailService;
import com.grolabs.caselist.service.email.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final JWTUtil jwtUtil;
    private final RefreshEntityRepository refreshEntityRepository;
    private final LoginHistoryRepository loginHistoryRepository;


    /**
     * join method
     *
     * @param joinDto A DTO containing the following fields:
     *                - username
     *                - email
     *                - phoneNum
     *                - site
     */
    public void joinUser(JoinDto joinDto) throws CloneNotSupportedException {
        System.out.println(joinDto);
        User user = new User();
        user.setUsername(joinDto.getUsername());
        user.setPassword(passwordEncoder.encode(joinDto.getUsername()));//Username과 동일한 값
        user.setEmail(joinDto.getEmail());
        user.setPhoneNum(joinDto.getPhoneNum());
        user.setSite(joinDto.getSite());
        user.setStatus(UserStatus.INACTIVE);
        user.setPasswordUpdateTime(LocalDateTime.now());

        if(userRepository.existsByUsername(joinDto.getUsername())){
            throw new CloneNotSupportedException("아이디 중복을 확인해주세요.");
        }
        else{
            userRepository.save(user);
        }
    }

    /**
     * check duplication
     *
     * @param type type of value (email or username)
     * @param value value to be checked for duplication
     * @return boolean
     */
    public boolean checkDuplication(String type, String value) {
        boolean exists;
        if ("username".equals(type)) {
            exists = userRepository.existsByUsername(value);
        } else if ("email".equals(type)) {
            exists = userRepository.existsByEmail(value);
        } else {
            return false;
        }
        return !exists;
    }


    /**
     * Sends an email with a password change token.
     *
     * @param accessToken The JWT access token provided in the request header to identify the user.
     * @return String
     */
    public String requestPassword(String accessToken) {
        String usertoken = accessToken.split(" ")[1];

        String username = jwtUtil.getUsername(usertoken);
        String email = userRepository.findByUsername(username).getEmail();

        String token = tokenService.generateToken(email);

        emailService.sendEmail(email, "비밀번호 재설정 요청",
                "위 인증 코드를 이용하여 비밀번호를 재설정하세요: " + token);

        return "비밀번호 재설정 이메일이 전송되었습니다.";
    }

    /**
     * validating the reset token and input details
     *
     * @param token A password reset token to validate the user's identity.
     *
     * @return boolean
     */
    public boolean validateToken(String token) {
        String email = tokenService.validateToken(token);
        if (email == null) {
            return false;
        }
        tokenService.invalidateToken(token);
        return true;
    }

    /**
     * Updates the User's password
     *
     * @param passwordEditDto A DTO containing the following fields:
     *                        - username
     *                        - currentPassword
     *                        - newPassword
     * @return boolean
     */
    public boolean updatePassword(PasswordEditDto passwordEditDto) {

        User user=userRepository.findByUsername(passwordEditDto.getUsername());
        if(user==null) {
            throw new NoSuchElementException("사용자가 존재하지 않습니다.");
        }
        //받아온 비밀번호와 저장된 비밀번호가 다를 경우 or 현재 비밀번호와 바꿀 비밀번호가 같을 경우 return false
        if(!passwordEncoder.matches(passwordEditDto.getCurrentPassword(),user.getPassword())|| passwordEditDto.getNewPassword().equals(passwordEditDto.getCurrentPassword())) {
            System.out.println("실패");
            throw new IllegalArgumentException("비밀번호를 변경할 수 없습니다");
        }
        else{
            user.setPassword(passwordEncoder.encode(passwordEditDto.getNewPassword()));
            user.setPasswordUpdateTime(LocalDateTime.now());
            userRepository.save(user);
            System.out.println("성공");
            return true;
        }
    }

    /**
     * admin join method
     *
     * @param joinDto A DTO containing the following fields:
     *                - username
     *                - email
     *                - phoneNum
     *                - site
     */
    public void managerJoin(JoinDto joinDto) {
        User user = new User();
        user.setUsername(joinDto.getUsername());
        user.setPassword(passwordEncoder.encode(joinDto.getUsername()));//Username과 동일한 값
        if(checkDuplication("email",joinDto.getEmail())){
            user.setEmail(joinDto.getEmail());
        } else{
            throw new IllegalArgumentException("이메일 중복을 확인해주세요.");
        }
        user.setPhoneNum(joinDto.getPhoneNum());
        user.setSite(joinDto.getSite());
        user.setStatus(UserStatus.INACTIVE);
        user.setPasswordUpdateTime(LocalDateTime.now());
        user.setUserType("ROLE_MANAGER");
        user.setAuthStatus(AuthStatus.AUTH_OK);

        if(userRepository.existsByUsername(joinDto.getUsername())){
            throw new IllegalArgumentException("아이디 중복을 확인해주세요.");
        }
        else{
            userRepository.save(user);
        }
    }

    public ResponseEntity<Void> closeLogout(String refresh) {


        if (refresh == null) {
            throw new IllegalArgumentException("토큰이 비어있습니다.");
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            //response status code
            throw new IllegalArgumentException("토큰이 만료됐습니다.");
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refreshToken")) {
            throw new IllegalArgumentException("refresh토큰이 아닙니다.");
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshEntityRepository.existsByRefresh(refresh);
        if (!isExist) {
            throw new IllegalArgumentException("유효하지 않는 토큰입니다.");
        }

        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
        refreshEntityRepository.deleteByRefresh(refresh);

        //로그인 히스토리 수정
        Long historyId = jwtUtil.getHistory(refresh);
        LoginHistory loginHistory = loginHistoryRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("log를 찾을 수 없습니다."));

        loginHistory.setLogoutTime(LocalDateTime.now());

        loginHistoryRepository.save(loginHistory);


        // User state수정
        User user = userRepository.findByUsername(jwtUtil.getUsername(refresh));
        user.setStatus(UserStatus.INACTIVE);

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
}
