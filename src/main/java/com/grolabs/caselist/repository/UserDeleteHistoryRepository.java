package com.grolabs.caselist.repository;

import com.grolabs.caselist.entity.UserCreateHistory;
import com.grolabs.caselist.entity.UserDeleteHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDeleteHistoryRepository extends JpaRepository<UserDeleteHistory,Long> {
    public UserDeleteHistory findByUserUsername(String username);
}
