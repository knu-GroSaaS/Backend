package com.grolabs.caselist.controller;


import com.grolabs.caselist.dto.History.CreateHistoryDto;
import com.grolabs.caselist.dto.History.DeleteHistoryDto;
import com.grolabs.caselist.dto.Response.GetUserResponseDto;
import com.grolabs.caselist.dto.user.UserAddDto;
import com.grolabs.caselist.dto.user.UserAuthorityDto;
import com.grolabs.caselist.dto.user.UserDeleteDto;
import com.grolabs.caselist.entity.LoginHistory;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.entity.UserCreateHistory;
import com.grolabs.caselist.entity.UserDeleteHistory;
import com.grolabs.caselist.repository.UserRepository;
import com.grolabs.caselist.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PutMapping("/manager/Authority")
    public void updateUserAuthority(@RequestBody UserAuthorityDto userAuthorityDto ){
       userService.updateUserAuthority(userAuthorityDto);
    }

    /**
     * GetUserList
     * @return List<GetUserResponseDto>
     */
    @GetMapping("/manager")
    public List<GetUserResponseDto> getAllUser(){
        return userService.findAll();
    }


    /**
     * Retrieve Dashboard User
     *
     * Handles HTTP GET requests to retrieve the user associated with the provided JWT token in the request header.
     * The token is used to identify and authenticate the user, ensuring secure access to the dashboard user information.
     *
     * @param accessToken
     * @return User
     */
    @GetMapping("/getuser")
    public User getUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        System.out.println(accessToken);
        return userService.getUser(accessToken);
    
    }

    /**
     * Add Dashboard User
     * Handles HTTP POST requests to add a new user to the dashboard.
     *
     * @param userAddDto A DTO containing the following fields:
     *                   - requestername: The name of the user making the request (e.g., manager).
     *                   - username: The name of the user to be added to the dashboard.
     * @return ResponseEntity<String> A response containing a success message.
     */
    @PostMapping("/manager/authcreate")
    public ResponseEntity<String> addUser(@RequestBody UserAddDto userAddDto){
        return ResponseEntity.ok(userService.UserCreate(userAddDto));
    }

    /**
     * Delete  User
     * Handles HTTP Delete requests to add a new user to the dashboard.
     *
     * @param userDeleteDto A DTO containing the following fields:
     *                   - requestername: The name of the user making the request (e.g., manager).
     *                   - username: The name of the user to be added to the dashboard.
     * @return ResponseEntity<String> A response containing a success message.
     * */
    @DeleteMapping("/manager/authdelete")
    public ResponseEntity<String> deleteUser(@RequestBody UserDeleteDto userDeleteDto){
        return ResponseEntity.ok(userService.UserDelete(userDeleteDto));
    }

    /**
     * Find loginHistory User
     * Handles HTTP POST requests to find user history in DB.
     *
     * @param accessToken in Header
     * @return List<LoginHistory>
     **/
    @GetMapping("/user/loghistory")
    public List<LoginHistory> findHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken){
        return userService.findHistory(accessToken);
    }

    /**
     * find all login history in login history table
     *
     * @return List<LoginHistory>
     */
    @GetMapping("/manager/loghistory")
    public List<LoginHistory> findAllHistory(){
        return userService.findAllHistory();
    }

    /**
     * find all user create history in user create history table
     *
     * @return List<CreateHistoryDto>
     */
    @GetMapping("/manager/createhis")
    public List<CreateHistoryDto> findAllCreateHistory(){
        return userService.findAllCreateHistory();
    }

    /**
     * find all user delete history in user delete history table
     *
     * @return List<DeleteHistoryDto>
     */
    @GetMapping("/manager/deletehis")
    public List<DeleteHistoryDto> findAllDeleteHistory(){
        return userService.findAllDeleteHistory();
    }


    /**
     * find all unAuthUser in User table
     *
     * @return List<User>
     */
    @GetMapping("/manager/auth")
    public List<User> unAuthUser() {
        return userService.unAuthUser();
    }

    /**
     * modify User Authentication (No_AUTH -> AUTH_OK)
     *
     * @param userId userId of User to be modified
     * @return ResponseEntity<String>
     */
    @PostMapping("/manager/auth/{userId}")
    public ResponseEntity<String> modifyUnAuthUser(@PathVariable Long userId){

        return ResponseEntity.ok(userService.modifyUnAuthUser(userId));
    }

}
