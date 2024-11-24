package com.grolabs.caselist.service;


import com.grolabs.caselist.dto.JoinDto;
import com.grolabs.caselist.dto.PasswordEditDto;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.entity.enums.UserStatus;
import com.grolabs.caselist.jwt.JWTUtil;
import com.grolabs.caselist.repository.UserRepository;
import com.grolabs.caselist.service.email.EmailService;
import com.grolabs.caselist.service.email.TokenService;
import lombok.RequiredArgsConstructor;
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


    public String requestPassword(String accessToken) {
        String usertoken = accessToken.split(" ")[1];

        String username = jwtUtil.getUsername(usertoken);
        String email = userRepository.findByUsername(username).getEmail();

        String token = tokenService.generateToken(email);

        emailService.sendEmail(email, "비밀번호 재설정 요청",
                "위 인증 코드를 이용하여 비밀번호를 재설정하세요: " + token);

        return "비밀번호 재설정 이메일이 전송되었습니다.";
    }

    public boolean updatePassword(String token, PasswordEditDto passwordEditDto) {

        String email = tokenService.validateToken(token);

        if (email == null) {
            throw new IllegalArgumentException("인증코드가 유효하지 않습니다.");
        }

        User user=userRepository.findByUsername(passwordEditDto.getUsername());
        if(user==null) {
            throw new NoSuchElementException("사용자가 존재하지 않습니다.");
        }
        //받아온 비밀번호와 저장된 비밀번호가 다를 경우 or 현재 비밀번호와 바꿀 비밀번호가 같을 경우 return false
        if(!passwordEncoder.matches(passwordEditDto.getCurrentPassword(),user.getPassword())|| passwordEditDto.getNewPassword().equals(passwordEditDto.getCurrentPassword())) {
            System.out.println("실패");
            throw new IllegalArgumentException("패스워드가 다릅니다");
        }
        else{
            user.setPassword(passwordEncoder.encode(passwordEditDto.getNewPassword()));
            user.setPasswordUpdateTime(LocalDateTime.now());
            userRepository.save(user);
            tokenService.invalidateToken(token);
            System.out.println("성공");
            return true;
        }
    }
}
