package com.project.sfoc.security.handler;

import com.project.sfoc.security.HttpUtil;
import com.project.sfoc.redis.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    private final HttpUtil httpUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String refreshToken = httpUtil.resolveRefreshToken(request);

        if (!refreshTokenService.isValid(refreshToken)) {
            throw new RuntimeException("일치하는 refresh token이 없음");
        }

        refreshTokenService.deleteRefreshToken(refreshToken);
        log.info("logout 완료");
    }

}
