package com.grolabs.caselist.repository;

import com.grolabs.caselist.entity.UserCreateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCreateHistoryRepository extends JpaRepository<UserCreateHistory, Long> {
    public UserCreateHistory findByUsername(String username);
}
