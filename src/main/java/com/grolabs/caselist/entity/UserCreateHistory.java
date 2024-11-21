package com.grolabs.caselist.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Entity
public class UserCreateHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requester")
    private Long requester;

    @Column(name = "creation")
    private String creation;

    @ManyToOne(fetch = FetchType.LAZY) // User 엔티티와 다대일 관계 설정
    @JoinColumn(name = "user_id", nullable = false) // 외래 키로 사용할 컬럼 정의
    private User user;

    @CreationTimestamp
    @Column(name = "creation_time")
    private Timestamp time;

}
