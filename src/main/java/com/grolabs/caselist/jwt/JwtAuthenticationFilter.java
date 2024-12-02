package com.grolabs.caselist.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grolabs.caselist.auth.PrincipalDetails;
import com.grolabs.caselist.dto.user.CustomUserDetails;
import com.grolabs.caselist.entity.LoginHistory;
import com.grolabs.caselist.entity.RefreshEntity;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.entity.enums.UserStatus;
import com.grolabs.caselist.exception.costom.PasswordException;
import com.grolabs.caselist.repository.LoginHistoryRepository;
import com.grolabs.caselist.repository.RefreshEntityRepository;
import com.grolabs.caselist.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.security.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;


/**spring security에 UsernamePasswordAuthenticationFilter가 있음
 * /login 요청시 username, password 전송(post)
 * UsernamePasswordAuthentication 동작
 **/
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final LoginHistoryRepository loginHistoryRepository;
    private final UserRepository userRepository;
    private final RefreshEntityRepository refreshEntityRepository;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        //클라이언트 요청에서 username, password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        //토큰에 담은 검증을 위한 매니저로 전달
        return authenticationManager.authenticate(authToken);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        // 유저 정보
        String username = authResult.getName();

        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        User user = userRepository.findByUsername(username);
        //로그인 기록 작성
        LoginHistory loginHistory = new LoginHistory(user);

        response.setContentType("application/json");// JSON 응답임을 명시
        response.setCharacterEncoding("UTF-8"); // UTF-8 설정


        // 비밀번호 바꾼 주기 확인
        if (shouldRequestPasswordChange(user.getPasswordUpdateTime())){
            response.setStatus(210); // 210코드 반환
        }
        loginHistoryRepository.save(loginHistory);

        //토큰 생성
        String access = jwtUtil.createJwt("accessToken", username, role, loginHistory.getLogId(), 36000000L); // 1시간
        String refresh = jwtUtil.createJwt("refreshToken", username, role, loginHistory.getLogId(), 2592000000L); // 3일


        //Refresh 토큰 저장
        addRefreshEntity(username, refresh, 2592000000L);

        // JSON 데이터를 담을 Map 생성
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("accessToken", access);
        responseMap.put("refreshToken", refresh);

        // JSON 직렬화
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseMap);

        // User State설정
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        // 응답 스트림에 JSON 작성
        response.getWriter().write(jsonResponse);
    }

    @Override
    protected  void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException faild) {

        response.setStatus(462);
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshEntityRepository.save(refreshEntity);
    }

    public boolean shouldRequestPasswordChange(LocalDateTime passwordUpdateTime) {
        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

        // 5분이 지났는지 확인
        Duration duration = Duration.between(passwordUpdateTime, now);
        return duration.toMinutes() >= 5; // 5분 이상 경과 여부
    }

//    private Cookie createCookie(String key, String value) {
//
//        Cookie cookie = new Cookie(key, value);
//        cookie.setMaxAge(24*60*60);
//        //cookie.setSecure(true) -> https통신할 때
//        //cookie.setPath("/") -> 적용될 범위 설정 가능
//        cookie.setHttpOnly(true); // 클라이언트단에서 쿠키에 접근하지 못하도록 막아야함
//
//        return cookie;
//    }

}
