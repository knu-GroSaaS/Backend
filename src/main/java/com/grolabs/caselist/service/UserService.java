package com.grolabs.caselist.service;


import com.grolabs.caselist.dto.user.GetUserDto;
import com.grolabs.caselist.dto.user.UserAuthorityDto;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.jwt.JWTUtil;
import com.grolabs.caselist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;

    private JWTUtil jwtUtil;


    @Transactional
    public void updateUserAuthority(UserAuthorityDto userAuthorityDto){
        User manager = userRepository.findByUsername(userAuthorityDto.getManagerName());

        if(manager.getUsertype().equals("MANAGER")){
            User user = userRepository.findByUsername(userAuthorityDto.getUserName());
            user.updateType(userAuthorityDto.getUserType());
        }
        else{
            throw new IllegalArgumentException("매니저 권한이 아닙니다.");
        }
    }

    public User getUser(GetUserDto getUserDto){
        String token = getUserDto.getToken();

        String username = jwtUtil.getUsername(token);

        return userRepository.findByUsername(username);
    }

}
