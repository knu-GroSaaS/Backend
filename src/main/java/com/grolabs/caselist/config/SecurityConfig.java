package com.grolabs.caselist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity//스프링 시큐리티 필터가 스프링 시큐리티 필터체인에 등록이 됨
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)//secure 어노테이션 활성화, preAuthorize 어노테이션 활성화
public class SecurityConfig {

    //해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
    @Bean
    public BCryptPasswordEncoder encodedPwd(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/user/**").authenticated() // /user라는 url로 들어오면 인증이 필요하다.//인증만 되면 들어갈 수 있는 주소
                        .requestMatchers("/manager/**").hasRole("MANAGER") // manager으로 들어오는 MANAGER 인증 또는 ADMIN인증이 필요하다는 뜻이다.
                        .anyRequest().permitAll() // 그리고 나머지 url은 전부 권한을 허용해준다.
                );

        http.formLogin(form -> form
                .loginPage("/login")//인증이 필요하면 loginform페이지로 이동
                .loginProcessingUrl("/login")// /login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해줌
                .defaultSuccessUrl("/"));// login이 완료되면 /로 이동

        return http.build();
    }
}
