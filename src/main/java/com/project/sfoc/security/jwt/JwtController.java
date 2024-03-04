package com.project.sfoc.security.jwt;

import com.project.sfoc.security.HttpUtil;
import com.project.sfoc.redis.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
@Slf4j
public class JwtController {

    private final JwtUtil jwtUtil;
    private final HttpUtil httpUtil;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/loginInfo")
    public ResponseEntity<UserInfo> oauthLoginInfo(@AuthenticationPrincipal UserInfo userInfo) {
        return ResponseEntity.ok(
                Optional.ofNullable(userInfo)
                        .orElse(UserInfo.of(null, "anonymous"))
        );
    }

    @GetMapping("/reissue")
    public ResponseEntity<String> refreshTokens(HttpServletResponse response,
                                                  @CookieValue(value = HttpUtil.REFRESH_TYPE) Cookie cookie) {
        String refreshToken = cookie.getValue();
        if (!refreshTokenService.isValid(refreshToken)) {
            throw new RuntimeException("유효하지 않은 refresh token");
        }

        log.info("refreshToken={}", refreshToken);

        UserInfo userInfo = jwtUtil.getUserInfo(refreshToken);
        TokenDto tokenDto = jwtUtil.createTokens(userInfo.id(), userInfo.role());
        log.info("token 재발급, access={}, refresh={}", tokenDto.accessToken(), tokenDto.refreshToken());

        response.setHeader(HttpHeaders.SET_COOKIE, httpUtil.createCookie(JwtUtil.REFRESH_TYPE, tokenDto.refreshToken()));
        return ResponseEntity.ok(tokenDto.accessToken());
    }

}
