package com.grolabs.caselist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinDto {
    private String username;
    private String email;
    private String phoneNum;
    private String site;
}
