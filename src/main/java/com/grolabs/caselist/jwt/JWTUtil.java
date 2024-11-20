package com.grolabs.caselist.jwt;


import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component // 0.12.3
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {

        // 받은 String을 암호화하는 부분
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token){
        // 우리서버에서 생성된 토큰이 맞는지 확인
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getUsertype(String token){
        // 우리서버에서 생성된 토큰이 맞는지 확인
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("usertype", String.class);
    }

    public Boolean isExpired(String token){
        // 만료됐는지 확인
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(String username, String userType, Long expirdMs){

        // JWT생성
        return Jwts.builder()
                .claim("username", username)
                .claim("userType", userType)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirdMs))
                .signWith(secretKey)
                .compact();
    }


}
