package com.grolabs.caselist.service;


import com.grolabs.caselist.dto.JoinDto;
import com.grolabs.caselist.dto.PasswordEditDto;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.entity.enums.UserStatus;
import com.grolabs.caselist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void joinUser(JoinDto joinDto) throws CloneNotSupportedException {
        System.out.println(joinDto);
        User user = new User();
        user.setUsername(joinDto.getUsername());
        user.setPassword(passwordEncoder.encode(joinDto.getUsername()));//Username과 동일한 값
        user.setEmail(joinDto.getEmail());
        user.setPhoneNum(joinDto.getPhoneNum());
        user.setSite(joinDto.getSite());
        user.setStatus(UserStatus.INACTIVE);

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

    public boolean updatePassword(PasswordEditDto passwordEditDto) {
        User user=userRepository.findByUsername(passwordEditDto.getUsername());
        if(user==null) {
            throw new NoSuchElementException("사용자가 존재하지 않습니다.");
        }
        //받아온 비밀번호와 저장된 비밀번호가 다를 경우 or 현재 비밀번호와 바꿀 비밀번호가 같을 경우 return false
        if(!passwordEncoder.matches(passwordEditDto.getCurrentPassword(),user.getPassword())|| passwordEditDto.getNewPassword().equals(passwordEditDto.getCurrentPassword())) {
            System.out.println("실패");
            return false;
        }
        else{
            user.setPassword(passwordEncoder.encode(passwordEditDto.getNewPassword()));
            userRepository.save(user);
            System.out.println("성공");
            return true;
        }
    }
}
