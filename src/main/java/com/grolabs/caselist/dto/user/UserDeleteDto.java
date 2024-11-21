package com.grolabs.caselist.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDeleteDto {
    public String requestername;
    public String username;
    public String deletion;
}
