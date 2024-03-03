package com.project.sfoc.security;

import com.project.sfoc.security.jwt.JwtUtil;
import com.project.sfoc.security.jwt.redis.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> refreshToken = jwtUtil.resolveRefreshToken(request);

        refreshToken.filter((token) -> jwtUtil.isNotExpired(token) && refreshTokenService.isValid(token))
                .ifPresentOrElse(refreshTokenService::deleteRefreshToken, this::handleLogoutFailure);

        log.info("logout 완료");
    }

    private void handleLogoutFailure() {
        throw new RuntimeException("일치하는 refresh token이 없습니다.");
    }
}
