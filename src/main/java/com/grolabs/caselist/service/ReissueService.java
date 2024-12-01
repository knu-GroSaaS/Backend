package com.grolabs.caselist.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grolabs.caselist.entity.RefreshEntity;
import com.grolabs.caselist.jwt.JWTUtil;
import com.grolabs.caselist.repository.RefreshEntityRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ReissueService {

    private final JWTUtil jwtUtil;

    private final RefreshEntityRepository refreshEntityRepository;


    /**
     * Refresh Token Reissue api
     * @param request The HTTP request containing the refresh token in the header
     * @param response The HTTP response to send the reissued tokens or error messages
     * @return ResponseEntity<?> A response entity indicating the success or failure of the operation
     */
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) throws IOException, java.io.IOException {
        //get refresh token
        String refresh = request.getHeader("refreshToken");

        if (refresh == null) {

            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refreshToken")) {

            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshEntityRepository.existsByRefresh(refresh);
        if (!isExist) {

            //response body
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getUsertype(refresh);
        Long history = jwtUtil.getHistory(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", username, role,history, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, history, 2592000000L);

        //delete current JWT
        refreshEntityRepository.deleteByRefresh(refresh);
        //Refresh 토큰 저장
        addRefreshEntity(username, newRefresh, 2592000000L);

        //response
        response.setContentType("application/json"); // JSON 응답임을 명시
        response.setCharacterEncoding("UTF-8"); // UTF-8 설정

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", newAccess);
        tokenMap.put("refreshToken", newRefresh);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(tokenMap);

        // 응답 스트림에 JSON 작성
        response.getWriter().write(jsonResponse);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Save a Refresh Token to the Database
     *
     * @param username username
     * @param refresh refresh token
     * @param expiredMs expiration time
     */
    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshEntityRepository.save(refreshEntity);
    }
}
