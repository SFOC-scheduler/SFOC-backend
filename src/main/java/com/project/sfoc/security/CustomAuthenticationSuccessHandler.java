package com.project.sfoc.security;

import com.project.sfoc.security.jwt.JwtUtil;
import com.project.sfoc.security.jwt.TokenDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        TokenDto tokenDto = createToken(authentication);
        log.info("token 발급, access={}, refresh={}", tokenDto.accessToken(), tokenDto.refreshToken());

        response.setHeader(HttpHeaders.SET_COOKIE, jwtUtil.createCookie(JwtUtil.REFRESH_TYPE, tokenDto.refreshToken()));
        getRedirectStrategy().sendRedirect(request, response, jwtUtil.getRedirectUrl(tokenDto.accessToken()));
    }

    private TokenDto createToken(Authentication authentication) {
        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();

        Long id = oauth2User.getId();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return jwtUtil.createTokens(id, role);
    }

}
