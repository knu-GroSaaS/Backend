package com.grolabs.caselist.entity;

import com.grolabs.caselist.entity.enums.UserStatus;
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
    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String email;
    private String phoneNum;
    private String usertype;
    private String site;
    private UserStatus status;
    @CreationTimestamp
    private Timestamp createTime;
    @UpdateTimestamp
    private Timestamp updateTime;
    private Timestamp deleteTime;

    public void updateType(String Type){
        this.usertype = Type;
    }
}
