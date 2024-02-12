package com.project.sfoc.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.lang.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private static final long ACCESS_EXPIRED_MS = 1_800_000;  // 30분
    private static final long REFRESH_EXPIRED_MS = 1_209_600_000;  // 2주
    private static final String ACCESS_HEADER = "Authorization";
    private static final String REFRESH_HEADER = "Authorization-refresh";
    private static final String ISSUER = "sfoc";
    private static final String ACCESS_SUBJECT = "AccessToken";
    private static final String REFRESH_SUBJECT = "RefreshToken";
    private static final String USER_CLAIMS = "user_claims";

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key()
                        .build()
                        .getAlgorithm()
        );
    }

    public String createAccessToken(Long id, String role) {
        UserClaims userClaims = UserClaims.of(id, role);
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(ACCESS_SUBJECT)
                .claim("user_claims", userClaims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRED_MS))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken() {
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(REFRESH_SUBJECT)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRED_MS))
                .signWith(secretKey)
                .compact();
    }

    public Boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    public UserClaims getUserClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .json(new JacksonDeserializer(Maps.of(USER_CLAIMS, UserClaims.class).build()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(USER_CLAIMS, UserClaims.class);
    }

}
