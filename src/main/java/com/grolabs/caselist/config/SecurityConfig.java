package com.grolabs.caselist.config;

import com.grolabs.caselist.jwt.JWTLogoutFilter;
import com.grolabs.caselist.jwt.JWTUtil;
import com.grolabs.caselist.jwt.JWTfilter;
import com.grolabs.caselist.jwt.JwtAuthenticationFilter;
import com.grolabs.caselist.repository.LoginHistoryRepository;
import com.grolabs.caselist.repository.RefreshEntityRepository;
import com.grolabs.caselist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JWTUtil jwtUtil;

    private final LoginHistoryRepository loginHistoryRepository;

    private final UserRepository userRepository;

    private final RefreshEntityRepository refreshEntityRepository;

    public SecurityConfig(JWTUtil jwtUtil, LoginHistoryRepository loginHistoryRepository, UserRepository userRepository, RefreshEntityRepository refreshEntityRepository) {
        this.jwtUtil = jwtUtil;
        this.loginHistoryRepository = loginHistoryRepository;
        this.userRepository = userRepository;
        this.refreshEntityRepository = refreshEntityRepository;
    }
    @Bean
    public BCryptPasswordEncoder encodedPwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean // authenticationManager를 IoC에 등록해줌.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sc -> sc.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않음
                .formLogin(AbstractHttpConfigurer::disable)//Form login 사용 x
                .httpBasic(AbstractHttpConfigurer::disable)//비활성화
                .addFilterAfter(new JWTfilter(jwtUtil), JwtAuthenticationFilter.class)
                .addFilterAt(new JwtAuthenticationFilter(authenticationManager, jwtUtil, loginHistoryRepository, userRepository, refreshEntityRepository), UsernamePasswordAuthenticationFilter.class)//AuthenticationManager argument
                .addFilterBefore(new JWTLogoutFilter(jwtUtil, refreshEntityRepository), LogoutFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/user/**", "/manager").hasRole("USER")
                        //.requestMatchers("/manager/**").hasRole("ADMIN")
                        .requestMatchers("/api/auth/refresh").permitAll()
                        .anyRequest().permitAll()

                );

        http
                .httpBasic((auth) -> auth.disable());
      
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://155.230.34.239"); // 클라이언트의 출처를 정확히 설정
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // 모든 메서드 허용
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
