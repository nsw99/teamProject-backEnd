package com.kh.auction.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;

import static com.kh.auction.user.Role.ADMIN;
import static com.kh.auction.user.Role.USER;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests();
        http.cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/api/admin/**").hasRole(ADMIN.name())
                .requestMatchers("/api/user/**").hasRole(USER.name())

                .requestMatchers(new AntPathRequestMatcher("/api/public/**")).permitAll()
                .anyRequest().authenticated();

        // JWT 토큰 생성부터 필터처리까지 전부 세팅
        // JWT 필터 등록
        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);

        return http.build();
    }

}
