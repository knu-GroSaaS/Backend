package com.grolabs.caselist.service;


import com.grolabs.caselist.dto.user.UserAddDto;
import com.grolabs.caselist.dto.user.UserAuthorityDto;
import com.grolabs.caselist.entity.LoginHistory;
import com.grolabs.caselist.entity.User;

import com.grolabs.caselist.jwt.JWTUtil;

import com.grolabs.caselist.entity.UserCreateHistory;
import com.grolabs.caselist.repository.LoginHistoryRepository;
import com.grolabs.caselist.repository.UserCreateHistoryRepository;

import com.grolabs.caselist.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final JWTUtil jwtUtil;

    private final LoginHistoryRepository loginHistoryRepository;

    private final UserCreateHistoryRepository userCreateHistoryRepository;


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


    public User getUser(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        String token = authorization.split(" ")[1];

        String username = jwtUtil.getUsername(token);

        return userRepository.findByUsername(username);
    }

    public String UserCreate(UserAddDto userAddDto){
        System.out.println(userAddDto.getRequestername());
        User manager = userRepository.findByUsername(userAddDto.getRequestername());
        System.out.println(manager.getUsername());
        if(manager.getUsertype().equals("ROLE_MANAGER")){
            Long managerId = manager.getId();
            String creation = userAddDto.getCreation();
            String username = userAddDto.getUsername();
            if (managerId == null || creation == null || username == null) {
                throw new IllegalArgumentException("항목을 모두 작성해 주세요");
            }
            User user = userRepository.findByUsername(username);

            UserCreateHistory userCreateHistory = new UserCreateHistory();
            userCreateHistory.setRequester(managerId);
            userCreateHistory.setUser(user);
            userCreateHistory.setCreation(creation);
            System.out.println(userCreateHistory);
            userCreateHistoryRepository.save(userCreateHistory);


            return "success";
        }
        else{
            return "매니저 권한이 아닙니다.";
        }
    }

    public List<LoginHistory> getAllHistory(String accessToken){
        String token = accessToken.split(" ")[1];
        String username = jwtUtil.getUsername(token);

        User user = userRepository.findByUsername(username);

        return loginHistoryRepository.findAllByUserId(user.getId());
    }

}
