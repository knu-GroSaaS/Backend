package com.grolabs.caselist.controller;


import com.grolabs.caselist.dto.user.UserAddDto;
import com.grolabs.caselist.dto.user.UserAuthorityDto;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.repository.UserRepository;
import com.grolabs.caselist.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PutMapping("/manager/Authority")
    public void updateUserAuthority(@RequestBody UserAuthorityDto userAuthorityDto){
       userService.updateUserAuthority(userAuthorityDto);
    }

    @GetMapping("/manager")
    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    @PostMapping("/manager")
    public String addUser(@RequestBody UserAddDto userAddDto){
        return userService.UserCreate(userAddDto);
    }

}
