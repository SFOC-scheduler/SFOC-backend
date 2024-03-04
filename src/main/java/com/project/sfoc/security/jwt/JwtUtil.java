package com.project.sfoc.security.jwt;

import com.project.sfoc.redis.RefreshToken;
import com.project.sfoc.redis.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.lang.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private static final long ACCESS_EXPIRED_MS = 1_800_000;  // 30분
    public static final long REFRESH_EXPIRED_MS = 1_209_600_000;  // 2주
    private static final String ISSUER = "sfoc";
    private static final String TOKEN_TYPE = "token_type";
    private static final String USER_INFO = "user_info";

    public static final String ACCESS_TYPE = "AccessToken";
    public static final String REFRESH_TYPE = "RefreshToken";

    private final SecretKey secretKey;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtUtil(@Value("${jwt.secret}") String secret, RefreshTokenRepository refreshTokenRepository) {
        secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key()
                        .build()
                        .getAlgorithm()
        );
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public TokenDto createTokens(Long id, String role) {
        return TokenDto.of(createAccessToken(id, role), createRefreshToken(id, role));
    }

    private String createAccessToken(Long id, String role) {
        UserInfo userInfo = UserInfo.of(id, role);
        return Jwts.builder()
                .issuer(ISSUER)
                .claim(TOKEN_TYPE, ACCESS_TYPE)
                .claim(USER_INFO, userInfo)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRED_MS))
                .signWith(secretKey)
                .compact();
    }

    private String createRefreshToken(Long id, String role) {
        UserInfo userInfo = UserInfo.of(id, role);
        String refreshToken = Jwts.builder()
                .issuer(ISSUER)
                .claim(TOKEN_TYPE, REFRESH_TYPE)
                .claim(USER_INFO, userInfo)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRED_MS))
                .signWith(secretKey)
                .compact();

        refreshTokenRepository.save(RefreshToken.of(id, refreshToken));

        return refreshToken;
    }

    public UserInfo getUserInfo(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .json(new JacksonDeserializer(Maps.of(USER_INFO, UserInfo.class).build()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(USER_INFO, UserInfo.class);
    }

}
