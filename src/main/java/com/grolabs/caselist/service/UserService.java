package com.grolabs.caselist.service;


import com.grolabs.caselist.dto.user.UserAddDto;
import com.grolabs.caselist.dto.user.UserAuthorityDto;
import com.grolabs.caselist.dto.user.UserDeleteDto;
import com.grolabs.caselist.entity.LoginHistory;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.entity.enums.AuthStatus;
import com.grolabs.caselist.jwt.JWTUtil;
import com.grolabs.caselist.entity.UserCreateHistory;
import com.grolabs.caselist.entity.UserDeleteHistory;
import com.grolabs.caselist.entity.enums.UserStatus;
import com.grolabs.caselist.repository.UserCreateHistoryRepository;
import com.grolabs.caselist.repository.UserDeleteHistoryRepository;
import com.grolabs.caselist.repository.LoginHistoryRepository;

import com.grolabs.caselist.repository.UserRepository;
import com.grolabs.caselist.service.email.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.NoSuchElementException;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final JWTUtil jwtUtil;

    private final LoginHistoryRepository loginHistoryRepository;

    private final UserCreateHistoryRepository userCreateHistoryRepository;

    private final UserDeleteHistoryRepository userDeleteHistoryRepository;

    private final EmailService emailService;


    public static final String MANAGER_NOT_FOUND = "매니저를 찾을 수 없습니다.";
    public static final String USER_NOT_FOUND = "유저를 찾을 수 없습니다.";
    public static final String EMAIL_SUBJECT = "GROCASS 권한 변경";
    public static final String EMAIL_TEXT = "권한이 삭제되었습니다.";




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

        User manager = userRepository.findByUsername(userAddDto.getRequestername());
        if(manager==null){
            throw new NoSuchElementException(MANAGER_NOT_FOUND);
        }
        if(manager.getUsertype().equals("ROLE_MANAGER")){
            Long managerId = manager.getId();
            String creation = userAddDto.getCreation();
            String username = userAddDto.getUsername();
            if (managerId == null || creation == null || username == null) {
                throw new IllegalArgumentException("항목을 모두 작성해 주세요");
            }
            User user = userRepository.findByUsername(username);
            if(user==null){
                throw new NoSuchElementException(USER_NOT_FOUND);
            }

            user.setAuthStatus(AuthStatus.AUTH_OK); //대시보드 권한 변경
            userRepository.save(user);

            UserCreateHistory userCreateHistory = new UserCreateHistory();
            userCreateHistory.setRequester(managerId);
            userCreateHistory.setUser(user);
            userCreateHistory.setCreation(creation);
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
            UserCreateHistory userCreateHistory = userCreateHistoryRepository.findByUserUsername(user.getUsername());
            if(userCreateHistory==null){
                throw new NoSuchElementException(USER_NOT_FOUND);
            }
            // 삭제 작업 수행
            userCreateHistoryRepository.delete(userCreateHistory);


            //user 정보 변경
            user.setStatus(UserStatus.SUSPENDED);
            user.setDeleteTime();
            user.setAuthStatus(AuthStatus.NOT_AUTH);
            userRepository.save(user);

            //사용자 삭제 테이블에 추가
            UserDeleteHistory userDeleteHistory = new UserDeleteHistory();
            userDeleteHistory.setRequester(manager.getId());
            userDeleteHistory.setUser(user);
            userDeleteHistory.setDeletion(userDeleteHistory.getDeletion());
            userDeleteHistoryRepository.save(userDeleteHistory);

            //email발송
            emailService.sendEmail(user.getEmail(),EMAIL_SUBJECT,EMAIL_TEXT);

            return "삭제 되었습니다.";
        }
        else{
            return "매니저 권한이 아닙니다.";
        }
    }


    public List<LoginHistory> findHistory(String accessToken){
        String token = accessToken.split(" ")[1];
        String username = jwtUtil.getUsername(token);

        User user = userRepository.findByUsername(username);

        return loginHistoryRepository.findAllByUserId(user.getId());
    }

    public List<LoginHistory> findAllHistory(){
        return loginHistoryRepository.findAll();
    }

    public List<User> unAuthUser() {

        return userRepository.findAllByAuthStatus(AuthStatus.NOT_AUTH);
    }
}
