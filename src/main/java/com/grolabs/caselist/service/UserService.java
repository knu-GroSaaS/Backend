package com.grolabs.caselist.service;


import com.grolabs.caselist.dto.user.UserAddDto;
import com.grolabs.caselist.dto.user.UserAuthorityDto;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.entity.UserCreateHistory;
import com.grolabs.caselist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;

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

    public String UserCreate(UserAddDto userAddDto){
        User manager = userRepository.findByUsername(userAddDto.getRequestername());
        if(manager.getUsertype().equals("MANAGER")){
            Long managerId = manager.getId();
            String creation = userAddDto.getCreation();
            String username = userAddDto.getUsername();
            if (managerId == null || creation == null || username == null) {
                return "항목을 모두 작성해 주세요";
            }
            User user = userRepository.findByUsername(username);

            UserCreateHistory userCreateHistory = new UserCreateHistory();
            userCreateHistory.setRequester(managerId);
            userCreateHistory.setUser(user);
            userCreateHistory.setCreation(creation);

            return "success";
        }
        else{
            return "매니저 권한이 아닙니다.";
        }
    }


}
