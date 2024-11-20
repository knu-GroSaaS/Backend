package com.grolabs.caselist.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grolabs.caselist.auth.PrincipalDetails;
import com.grolabs.caselist.dto.user.CustomUserDetails;
import com.grolabs.caselist.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


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
        String role = auth.getAuthority();

        //토큰 생성
        String access = jwtUtil.createJwt("access", username, role, 600000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L);
        response.setContentType("application/json"); // JSON 응답임을 명시
        response.setCharacterEncoding("UTF-8"); // UTF-8 설정

        // JSON 데이터를 담을 Map 생성
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("access", access);
        tokenMap.put("refresh", refresh);

        // JSON 직렬화
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(tokenMap);

        // 응답 스트림에 JSON 작성
        response.getWriter().write(jsonResponse);
    }

    @Override
    protected  void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException faild) {

        response.setStatus(401);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true) -> https통신할 때
        //cookie.setPath("/") -> 적용될 범위 설정 가능
        cookie.setHttpOnly(true); // 클라이언트단에서 쿠키에 접근하지 못하도록 막아야함

        return cookie;
    }

}
