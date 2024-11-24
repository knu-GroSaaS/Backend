package com.grolabs.caselist.service;


import com.grolabs.caselist.dto.user.UserAddDto;
import com.grolabs.caselist.dto.user.UserAuthorityDto;
import com.grolabs.caselist.dto.user.UserDeleteDto;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.entity.UserCreateHistory;
import com.grolabs.caselist.entity.UserDeleteHistory;
import com.grolabs.caselist.entity.enums.UserStatus;
import com.grolabs.caselist.repository.UserCreateHistoryRepository;
import com.grolabs.caselist.repository.UserDeleteHistoryRepository;
import com.grolabs.caselist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    public static final String MANAGER_NOT_FOUND = "매니저를 찾을 수 없습니다.";
    public static final String USER_NOT_FOUND = "유저를 찾을 수 없습니다.";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCreateHistoryRepository userCreateHistoryRepository;
    @Autowired
    private UserDeleteHistoryRepository userDeleteHistoryRepository;

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
        if(manager==null){
            throw new NoSuchElementException(MANAGER_NOT_FOUND);
        }
        if(manager.getUsertype().equals("ROLE_MANAGER")){
            Long managerId = manager.getId();
            String creation = userAddDto.getCreation();
            String username = userAddDto.getUsername();
            if (managerId == null || creation == null || username == null) {
                return "항목을 모두 작성해 주세요";
            }
            User user = userRepository.findByUsername(username);
            if(user==null){
                throw new NoSuchElementException(USER_NOT_FOUND);
            }

            UserCreateHistory userCreateHistory = new UserCreateHistory();
            userCreateHistory.setRequester(managerId);
            userCreateHistory.setUser(user);
            userCreateHistory.setCreation(creation);
            System.out.println(userCreateHistory);
            userCreateHistoryRepository.save(userCreateHistory);

            return "추가 되었습니다.";
        }
        else{
            return "매니저 권한이 아닙니다.";
        }
    }

    public String UserDelete(UserDeleteDto userDeleteDto){
        User manager = userRepository.findByUsername(userDeleteDto.getRequestername());
        if(manager==null){
            throw new NoSuchElementException(MANAGER_NOT_FOUND);
        }

        if(manager.getUsertype().equals("ROLE_MANAGER")){
            User user = userRepository.findByUsername(userDeleteDto.getUsername());
            if(user==null){
                throw new NoSuchElementException(USER_NOT_FOUND);
            }

            //사용자 생성테이블에서 삭제
            UserCreateHistory userCreateHistory = userCreateHistoryRepository.findByUsername(user.getUsername());
            if(userCreateHistory==null){
                throw new NoSuchElementException(USER_NOT_FOUND);
            }
            // 삭제 작업 수행
            userCreateHistoryRepository.delete(userCreateHistory);

            //user 정보 변경
            user.setStatus(UserStatus.SUSPENDED);
            user.setDeleteTime();
            userRepository.save(user);


            UserDeleteHistory userDeleteHistory = new UserDeleteHistory();
            userDeleteHistory.setRequester(manager.getId());
            userDeleteHistory.setUser(user);
            userDeleteHistoryRepository.save(userDeleteHistory);

            return "삭제 되었습니다.";
        }
        else{
            return "매니저 권한이 아닙니다.";
        }
    }


}
