package com.project.sfoc.security.jwt;

import com.project.sfoc.security.jwt.redis.RefreshToken;
import com.project.sfoc.security.jwt.redis.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.lang.Maps;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtil {

    private static final long ACCESS_EXPIRED_MS = 1_800_000;  // 30분
    public static final long REFRESH_EXPIRED_MS = 1_209_600_000;  // 2주
    private static final String ISSUER = "sfoc";
    private static final String TOKEN_TYPE = "token_type";
    private static final String USER_INFO = "user_info";
    private static final String TOKEN_PARAM = "token";
    private static final String CALLBACK_URL = "https://sfoc-scheduler.github.io/SFOC-frontend";

    public static final String ACCESS_HEADER = "Authorization";
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

    public boolean isNotExpired(String token) {
        try {
            return !Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        }
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

    public String createCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(1_209_600)  // 2주
                .build()
                .toString();
    }

    public Optional<String> resolveAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(ACCESS_HEADER))
                .filter(authorization -> authorization.startsWith("Bearer "))
                .map(authorization -> authorization.split(" ")[1]);
    }

    public Optional<String> resolveRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> cookie.getName().equals(REFRESH_TYPE))
                        .findFirst()
                        .map(Cookie::getValue));
    }

    public String getRedirectUrl(String accessToken) {
        return UriComponentsBuilder.fromUriString(CALLBACK_URL)
                .queryParam(TOKEN_PARAM, accessToken)
                .build()
                .toUriString();
    }

}
