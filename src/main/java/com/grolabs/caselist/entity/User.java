package com.grolabs.caselist.entity;

import com.grolabs.caselist.entity.enums.UserStatus;
import com.grolabs.caselist.entity.enums.UserType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

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

    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false)
    private String usertype = UserType.USER.toString();

    @Column(nullable = false)
    private String site;

    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp createTime;

    @UpdateTimestamp
    private Timestamp updateTime;


    private Timestamp deleteTime;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column(unique = true)
    private String emailVerificationToken;

    // 이메일 검증 상태를 업데이트하는 메서드
    public void verifyEmail() {
        this.emailVerified = true;
        this.emailVerificationToken = null; // 검증 후 토큰 제거
    }

    public void updateType(String Type){
        this.usertype = Type;
    }

}
