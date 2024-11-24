package com.grolabs.caselist.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY) // User 엔티티와 다대일 관계 설정
    @JoinColumn(name = "user_id", nullable = false) // 외래 키로 사용할 컬럼 정의
    private User user;

    @CreationTimestamp
    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    public LoginHistory(User user){
        this.user = user;
        this.loginTime = LocalDateTime.now();
    }

    public LoginHistory() {

    }
}
