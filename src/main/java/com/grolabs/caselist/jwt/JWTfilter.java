package com.grolabs.caselist.jwt;

import com.grolabs.caselist.dto.user.CustomUserDetails;
import com.grolabs.caselist.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTfilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTfilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String requestURI = request.getRequestURI();
            // request에서 헤더 찾음
            String authorization = request.getHeader("Authorization");

            if (requestURI.startsWith("/swagger-ui") || requestURI.startsWith("/v3/api-docs")) {
                filterChain.doFilter(request, response);
                return;
            }

            // 헤더 검증
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                System.out.println("token null");
                // 임시 처리
                if (requestURI.startsWith("/api/auth")){
                    response.setStatus(401);
                }
                // 다음 필터로 넘겨주는 작업
                filterChain.doFilter(request, response);

                return;
            }
            //Bearer 제거
            String token = authorization.split(" ")[1];

            //시간 검증
            if (jwtUtil.isExpired(token)) {
                System.out.println("token expired");
                response.setStatus(461);
                filterChain.doFilter(request, response);

                return;
            }

            System.out.println("authorization now");

            // 여기서부턴 토큰 확인 완료, 잠깐 세션 생성해서 진행
            String username = jwtUtil.getUsername(token);
            String usertype = jwtUtil.getUsertype(token);


            User user = new User();
            user.setUsername(username);
            user.setPassword("temppassword");
            user.setUsertype(usertype);

            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            //스프링 시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            //세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // 그다음 필터로 넘겨주면 됨
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // JWT 만료 시 461 상태 코드 반환
            System.out.println("Token expired: " + e.getMessage());
            response.setStatus(461);
            response.getWriter().write("{\"error\": \"Token expired\"}");
            response.getWriter().flush();
        }
    }
}
