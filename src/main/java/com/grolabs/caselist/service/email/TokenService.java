package com.grolabs.caselist.service.email;

import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {

    @Autowired
    private UserRepository userRepository;

    /**
     * generate token
     *
     * @param email to receive token
     * @return String token
     */
    public String generateToken(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("해당 이메일을 사용하는 사용자가 존재하지 않습니다.");
        }

        User user = optionalUser.get();
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10); // 토큰 만료 시간 설정 (10분)

        // 토큰과 만료 시간 저장
        user.setResetToken(token);
        user.setTokenExpiryTime(expiryTime);
        userRepository.save(user);

        return token;
    }

    /**
     * validate token
     *
     * @param token to verify
     * @return String email
     */
    public String validateToken(String token) {
        Optional<User> optionalUser = userRepository.findByResetToken(token);

        if (optionalUser.isEmpty()) {
            return null; // 토큰이 존재하지 않음
        }

        User user = optionalUser.get();
        if (user.getTokenExpiryTime().isBefore(LocalDateTime.now())) {
            // 토큰 만료 처리
            user.setResetToken(null);
            user.setTokenExpiryTime(null);
            userRepository.save(user);
            return null; // 만료된 토큰
        }

        return user.getEmail(); // 유효한 토큰의 이메일 반환
    }

    /**
     *Invalidates a reset token.
     *
     * @param token the reset token to be invalidated
     */
    public void invalidateToken(String token) {
        Optional<User> optionalUser = userRepository.findByResetToken(token);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setResetToken(null);
            user.setTokenExpiryTime(null);
            userRepository.save(user);
        }
    }
}
