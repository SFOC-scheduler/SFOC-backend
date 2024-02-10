package com.project.sfoc.security;

import com.project.sfoc.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String TOKEN = "token";
    private static final String REDIRECT_URL = "http://localhost:3000/login/callback";

    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        String token = createToken(authentication);
        getRedirectStrategy().sendRedirect(request, response, getRedirectUrl(token));
    }

    private String createToken(Authentication authentication) {
        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();

        Long id = oauth2User.getId();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return jwtUtil.createJwt(id, role);
    }

    private String getRedirectUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_URL)
                .queryParam(TOKEN, token)
                .build().toUriString();
    }
}
