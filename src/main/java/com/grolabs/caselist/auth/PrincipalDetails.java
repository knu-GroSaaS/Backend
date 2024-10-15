package com.grolabs.caselist.auth;

//시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행 시킴
//로그인을 진행이 완료가 되면 시큐리티 session을 만들어줌 (security contextholder)
//오브젝트 타입 => Authentication타입 객체
//Authentication안에 User정보가 있어야 됨
//User오브젝트 타입 => UserDetails 타입 객체

//Security Session => Authentication => UserDetails(PrincipalDetiails)

import com.grolabs.caselist.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class PrincipalDetails implements UserDetails {

    private User user;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    //해당 user의 권한을 리턴하는 곳!!
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getUsertype();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //계정 활성화
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //비밀번호 사용기간이 오래 되었니?
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
