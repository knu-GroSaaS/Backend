package com.grolabs.caselist.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class LoginDto {
    private String username;
    private String password;
}
