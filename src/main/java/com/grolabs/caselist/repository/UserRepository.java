package com.grolabs.caselist.repository;

import com.grolabs.caselist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

//CRUD함수를 JpaRepository가 들고 잇음.
//@Repositry가 없어도 IoC됨 , 상속했기 때문
public interface UserRepository extends JpaRepository<User, Integer> {
    //findBy규칙 -> Username문법
    //select * from user where username = ? (<-username)
    public User findByUsername(String username);
}
