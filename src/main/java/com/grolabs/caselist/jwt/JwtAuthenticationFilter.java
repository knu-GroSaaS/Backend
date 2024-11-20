package com.grolabs.caselist.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grolabs.caselist.auth.PrincipalDetails;
import com.grolabs.caselist.dto.user.CustomUserDetails;
import com.grolabs.caselist.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;


/**spring security에 UsernamePasswordAuthenticationFilter가 있음
 * /login 요청시 username, password 전송(post)
 * UsernamePasswordAuthentication 동작
 **/
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 1. usename, password get
        User user;
        ObjectMapper mapper = new ObjectMapper(); // json data parsing
        try {
            user = mapper.readValue(request.getInputStream(),User.class);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()); // token

            // 2. login try
            Authentication auth = authenticationManager.authenticate(authenticationToken); // princialDetailsService call

            // 3. PrincipalDetails을 세션에 담음(인가 관리를 위해)
            PrincipalDetails principalDetails = (PrincipalDetails) auth.getPrincipal();
            System.out.println("login success");

            return auth;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        // 유저 정보
        String username = authResult.getName();


        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String usertype = auth.getAuthority();

        //토큰 생성
        String access = jwtUtil.createJwt("access", username, usertype, 600000L);
        String refresh = jwtUtil.createJwt("refresh", username, usertype, 86400000L);

        //응답 설정
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected  void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException faild) {

        response.setStatus(401);
    }


}
