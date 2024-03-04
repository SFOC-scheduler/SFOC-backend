package com.project.sfoc.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Optional;

@Component
public class HttpUtil {

    private static final Long maxAgeSeconds = 1_209_600L;
    private static final String CALLBACK_URL = "https://sfoc-scheduler.github.io/SFOC-frontend";
    private static final String TOKEN_PARAM = "token";
    public static final String ACCESS_HEADER = "Authorization";
    public static final String REFRESH_TYPE = "RefreshToken";

    public String createCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(maxAgeSeconds)  // 2주
                .path("/")
                .build()
                .toString();
    }

    public String resolveAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(ACCESS_HEADER))
                .filter(authorization -> authorization.startsWith("Bearer "))
                .map(authorization -> authorization.split(" ")[1])
                .orElseThrow(() -> new IllegalArgumentException("access token 없음"));
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> cookie.getName().equals(REFRESH_TYPE))
                        .findFirst()
                        .map(Cookie::getValue))
                .orElseThrow(() -> new IllegalArgumentException("refresh token 없음"));
    }

    public String getRedirectUrl(String accessToken) {
        return UriComponentsBuilder.fromUriString(CALLBACK_URL)
                .queryParam(TOKEN_PARAM, accessToken)
                .build()
                .toUriString();
    }
}
