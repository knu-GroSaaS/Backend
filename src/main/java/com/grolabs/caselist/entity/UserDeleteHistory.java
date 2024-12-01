package com.grolabs.caselist.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@Entity
public class UserDeleteHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requester")
    private Long requester;

    // User 엔티티와 일대일 관계 설정
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true) // 외래 키로 사용할 컬럼 정의
    private User user;

    @CreationTimestamp
    @Column(name = "creation_time")
    private Timestamp time;
}
