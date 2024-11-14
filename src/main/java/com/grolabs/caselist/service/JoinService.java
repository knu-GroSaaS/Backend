package com.grolabs.caselist.service;


import com.grolabs.caselist.dto.JoinDto;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void joinUser(JoinDto joinDto) {
        System.out.println(joinDto);
        User user = new User();
        user.setUsername(joinDto.getUsername());
        user.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        user.setEmail(joinDto.getEmail());
        user.setPhoneNum(joinDto.getPhoneNum());
        user.setSite(joinDto.getSite());
        userRepository.save(user);
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
}
