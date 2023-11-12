package com.kh.auction.security;


import com.kh.auction.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


@Service
public class TokenProvider {

    private static final String SECRET_KEY="FlRpX30MqDbiAkmlfArbrmVkDD4RqISskGZmBFax5oGVxzXXWUzTR5JyskiHMIV9M10icegkpi46AdvrcX1E6CmTUBc6IFbTPiD";

    public String create(Member member) {
        // 토큰 생성 -> 기한 지정 가능 (1일)
        Date expiryDate = Date.from(Instant.now().plus(10, ChronoUnit.DAYS));
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY) // header에 들어갈 내용
                .setSubject(member.getId()) // <-- 여기부터 payload에 들어갈 내용
                .setIssuer("teamp app")
                .claim("authority", member.getAuthority())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .compact();

    }

    public String validateAndGetUserId(String token) { // 토큰을 디코딩, 파싱 및 위조여부 확인
        Claims claims = Jwts.parser()
                            .setSigningKey(SECRET_KEY)
                            .parseClaimsJws(token)
                            .getBody();
        return claims.getSubject(); // id를 반환
    }

    public String validateAndGetUserAuthority(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return (String) claims.get("authority");
    }

}
