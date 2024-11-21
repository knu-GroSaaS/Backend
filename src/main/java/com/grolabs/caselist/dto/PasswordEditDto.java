package com.grolabs.caselist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordEditDto {
    String username;
    String currentPassword;
    String newPassword;
}
