package com.grolabs.caselist.service;


import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public void joinUser(User user) {
        System.out.println(user);
        String rawPassword = user.getPassword();
        String enPassword=passwordEncoder.encode(rawPassword);
        user.setPassword(enPassword);
        userRepository.save(user);
    }
}
