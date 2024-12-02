package com.grolabs.caselist.service;


import com.grolabs.caselist.dto.History.CreateHistoryDto;
import com.grolabs.caselist.dto.History.DeleteHistoryDto;
import com.grolabs.caselist.dto.Response.GetUserResponseDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.stream.Collectors;


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


    /**
     *
     * @param userAuthorityDto A DTO containing the following fields:
     *                         - managerName
     *                         - userName
     *                         - userType
     */
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



  /**
     * get User
     *
     * @param request The HTTP request containing the JWT token in the header
     * @return User
     */
    public User getUser(String request){
        System.out.println(request);
        String token = request.split(" ")[1];

        String username = jwtUtil.getUsername(token);


        return userRepository.findByUsername(username);
    }

    /**
     * Add User Roles for the Dashboard
     *
     * @param userAddDto A DTO containing the following fields:
     *                   - requestername
     *                   - username
     *                   - creation
     * @return String
     */
    public String UserCreate(UserAddDto userAddDto){

        User manager = userRepository.findByUsername(userAddDto.getRequestername());
        if(manager==null){
            throw new NoSuchElementException(MANAGER_NOT_FOUND);
        }
        if(manager.getUsertype().equals("ROLE_MANAGER")){
            Long managerId = manager.getId();
            String username = userAddDto.getUsername();
            if (managerId == null || username == null) {
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
            userCreateHistoryRepository.save(userCreateHistory);

            return "추가 되었습니다.";
        }
        else{
            return "매니저 권한이 아닙니다.";
        }
    }

    /**
     * Delete User Roles for the Dashboard
     *
     * @param userDeleteDto A DTO containing the following fields:
     *                      - requestername
     *                      - username
     * @return String
     */
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
            //user.setStatus(UserStatus.SUSPENDED);
            //user.setDeleteTime();
            user.setAuthStatus(AuthStatus.NOT_AUTH);
            userRepository.save(user);

            //사용자 삭제 테이블에 추가
            UserDeleteHistory userDeleteHistory = new UserDeleteHistory();
            userDeleteHistory.setRequester(manager.getId());
            userDeleteHistory.setUser(user);
            userDeleteHistoryRepository.save(userDeleteHistory);

            //email발송
            emailService.sendEmail(user.getEmail(),EMAIL_SUBJECT,EMAIL_TEXT);

            return "삭제 되었습니다.";
        }
        else{
            throw new IllegalArgumentException("매니저 권한이 아닙니다.");
        }
    }

    /**
     * find History by accessToken
     *
     * @param accessToken the JWT token in the header
     * @return List<LoginHistory>
     */
    public List<LoginHistory> findHistory(String accessToken){
        String token = accessToken.split(" ")[1];
        String username = jwtUtil.getUsername(token);

        User user = userRepository.findByUsername(username);
        if (user == null){
            throw new NoSuchElementException("해당 유저가 존재하지 않습니다.");
        }

        return loginHistoryRepository.findAllByUserId(user.getId());
    }

    /**
     * Return All login History
     *
     * @return List<LoginHistory>
     */
    public List<LoginHistory> findAllHistory(){

        return loginHistoryRepository.findAll();
    }

    /**
     * find Create History by accessToken
     *
     * @param accessToken the JWT token in the header
     * @return UserCreateHistory
     */
    public UserCreateHistory findCreateHistory(String accessToken){
        String token = accessToken.split(" ")[1];
        String username = jwtUtil.getUsername(token);
        User user = userRepository.findByUsername(username);

        return userCreateHistoryRepository.findByUserUsername(username);
    }

     /**
     * Return All UserCreate History
     *
     * @return List<CreateHistoryDto>
     */
    public List<CreateHistoryDto> findAllCreateHistory(){
        List<UserCreateHistory> userCreateHistorys = userCreateHistoryRepository.findAll();
        List<CreateHistoryDto> userCreateHistoryDtos = new ArrayList<>();
        for (UserCreateHistory userCreateHistory : userCreateHistorys) {
            String managerName = userRepository.findById(userCreateHistory.getRequester()).get().getUsername();
            String username = userRepository.findById(userCreateHistory.getUser().getId()).get().getUsername();
            userCreateHistoryDtos.add(new CreateHistoryDto(managerName,username,userCreateHistory.getTime()));
        }
        return userCreateHistoryDtos;
    }

    /**
     * Return All UserDelete History
     *
     * @return List<DeleteHistoryDto>
     */
    public List<DeleteHistoryDto> findAllDeleteHistory(){
        List<UserDeleteHistory> userDeleteHistorys = userDeleteHistoryRepository.findAll();
        List<DeleteHistoryDto> userDeleteHistoryDtos = new ArrayList<>();
        for (UserDeleteHistory userDeleteHistory : userDeleteHistorys) {
            String managerName = userRepository.findById(userDeleteHistory.getRequester()).get().getUsername();
            String username = userRepository.findById(userDeleteHistory.getUser().getId()).get().getUsername();
            userDeleteHistoryDtos.add(new DeleteHistoryDto(managerName,username,userDeleteHistory.getTime()));
        }
        return userDeleteHistoryDtos;
    }

    /**
     * find all User to have NOT_AUTH
     * @return List<User>
     */
    public List<User> unAuthUser() {

        return userRepository.findAllByAuthStatus(AuthStatus.NOT_AUTH);
    }

    /**
     * modify User Authority
     * @param userId User to be modified
     * @return String
     */
    public String modifyUnAuthUser(Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NoSuchElementException("해당 유저를 찾을 수 없습니다.")
        );

        user.setAuthStatus(AuthStatus.AUTH_OK);

        return "유저 권한을 설정했습니다.";
    }

    /**
     * GetUserList
     * @return List<GetUserResponseDto>
     */
    public List<GetUserResponseDto> findAll() {

        return userRepository.findAll().stream()
                .map(GetUserResponseDto::new)
                .collect(Collectors.toList());
    }
}
