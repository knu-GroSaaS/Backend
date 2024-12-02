package com.grolabs.caselist.dto.Logout;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogoutDto {
    String refreshToken;
}
