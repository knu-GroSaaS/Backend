package com.grolabs.caselist.jwt;

import com.grolabs.caselist.entity.LoginHistory;
import com.grolabs.caselist.repository.LoginHistoryRepository;
import com.grolabs.caselist.repository.RefreshEntityRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.time.LocalDateTime;

public class JWTLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;

    private final RefreshEntityRepository refreshEntityRepository;

    private final LoginHistoryRepository loginHistoryRepository;

    public JWTLogoutFilter(JWTUtil jwtUtil, RefreshEntityRepository refreshEntityRepository, LoginHistoryRepository loginHistoryRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshEntityRepository = refreshEntityRepository;
        this.loginHistoryRepository = loginHistoryRepository;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        //logout 경로인지 확인
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

//        //get refresh token
//        String refresh = null;
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//
//            if (cookie.getName().equals("refresh")) {
//
//                refresh = cookie.getValue();
//            }
//        }

        String refresh = request.getHeader("refreshToken");

        //refresh null check
        if (refresh == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refreshToken")) {

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshEntityRepository.existsByRefresh(refresh);
        if (!isExist) {

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
        refreshEntityRepository.deleteByRefresh(refresh);

        //로그인 히스토리 수정
        Long historyId = jwtUtil.getHistory(refresh);
        LoginHistory loginHistory = loginHistoryRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("log를 찾을 수 없습니다."));

        loginHistory.setLogoutTime(LocalDateTime.now());

        loginHistoryRepository.save(loginHistory);

//        //Refresh 토큰 Cookie 값 0
//        Cookie cookie = new Cookie("refresh", null);
//        cookie.setMaxAge(0);
//        cookie.setPath("/");
//
//        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}