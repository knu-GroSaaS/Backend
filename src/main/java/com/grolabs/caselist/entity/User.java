package com.grolabs.caselist.entity;

import com.grolabs.caselist.entity.enums.UserStatus;
import com.grolabs.caselist.entity.enums.UserType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String phoneNum;

    @Column
    private String usertype = UserType.ROLE_USER.toString();

    @Column
    private String site;

    @Column
    private UserStatus status;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp createTime;

    @UpdateTimestamp
    private Timestamp updateTime;

    private LocalDateTime passwordUpdateTime;

    private Timestamp deleteTime;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column(unique = true)
    private String emailVerificationToken;

    private String resetToken; // 비밀번호 재설정 토큰

    private LocalDateTime tokenExpiryTime; // 토큰 만료 시간

    // 이메일 검증 상태를 업데이트하는 메서드
    public void verifyEmail() {
        this.emailVerified = true;
        this.emailVerificationToken = null; // 검증 후 토큰 제거
    }

    public void setDeleteTime() {
        this.deleteTime = Timestamp.from(Instant.now());
    }


    public void updateType(String Type){
        this.usertype = Type;
    }

}
