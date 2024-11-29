package com.grolabs.caselist.dto.Response;


import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserResponseDto {
    public Long userId;

    public String username;

    public String email;

    public String phoneNum;

    public String userType;

    public String site;

    public UserStatus status;

    public Timestamp createTime;

    public Timestamp updateTime;

    public LocalDateTime passwordUpdateTime;

    public GetUserResponseDto(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phoneNum = user.getPhoneNum();
        this.userType = user.getUsertype();
        this.site = user.getSite();
        this.status = user.getStatus();
        this.createTime = user.getCreateTime();
        this.updateTime = user.getUpdateTime();
        this.passwordUpdateTime = user.getPasswordUpdateTime();
    }
}
