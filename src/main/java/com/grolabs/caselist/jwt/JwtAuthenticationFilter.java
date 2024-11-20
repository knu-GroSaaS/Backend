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

        Long EXPIRATION_TIME = 60000*10L;
        //jwt token 생성 후 response header에 담아 전송

        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();

        String username = customUserDetails.getUsername();

        // 권한 뽑아내는 과정
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(username, role, EXPIRATION_TIME);
        // HTTP 인증 방식 RFC 7235에 따라 인증 헤더는 Bearer라는 형태를 가져야한다.
        response.addHeader("Authorization", "Bearer " + token);
    }

    @Override
    protected  void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException faild) {

        response.setStatus(401);
    }


}
