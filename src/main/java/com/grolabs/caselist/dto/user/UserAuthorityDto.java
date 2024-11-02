package com.grolabs.caselist.dto.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthorityDto {
    private String managerName;
    private String userName;
    private String userType;

}
