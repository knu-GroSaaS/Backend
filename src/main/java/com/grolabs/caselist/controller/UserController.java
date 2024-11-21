package com.grolabs.caselist.controller;


import com.grolabs.caselist.dto.user.UserAddDto;
import com.grolabs.caselist.dto.user.UserAuthorityDto;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.repository.UserRepository;
import com.grolabs.caselist.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

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

    @GetMapping("/manager")
    public List<User> getAllUser(){
        return userRepository.findAll();
    }


    @GetMapping("/manager/getuser")
    public User getUser(HttpServletRequest request) {
        return userService.getUser(request);
    }
    /**
    * Add Dashboard User
    * Handles HTTP POST requests to add a new user to the dashboard.
    *
    * @param userAddDto A DTO containing the following fields:
    *                   - requestername: The name of the user making the request (e.g., manager).
    *                   - username: The name of the user to be added to the dashboard.
    *                   - creation: The timestamp or identifier for the user creation process.
    * @return ResponseEntity<String> A response containing a success message.
     * */
    @PostMapping("/manager")
    public ResponseEntity<String> addUser(@RequestBody UserAddDto userAddDto){
        return ResponseEntity.ok(userService.UserCreate(userAddDto));
    }

}
