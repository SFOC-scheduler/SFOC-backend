package com.project.sfoc.security.jwt;

import com.project.sfoc.security.jwt.redis.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("접속자 ip={}", request.getRemoteAddr());
        log.info("요청 URI={}", request.getRequestURI());

        Optional<String> accessToken = jwtUtil.resolveAccessToken(request);
        Optional<String> refreshToken = jwtUtil.resolveRefreshToken(request);

        accessToken.filter(jwtUtil::isNotExpired)
                .map(jwtUtil::getUserInfo)
                .map(userInfo -> createAuthenticationToken(userInfo, request))
                .ifPresentOrElse((auth) -> setAuthentication(auth, request, response, filterChain),
                        () -> handleReIssuanceOrFailure(refreshToken, request, response, filterChain));
    }

    private Authentication createAuthenticationToken(UserInfo userInfo, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userInfo,
                null,
                List.of(new SimpleGrantedAuthority(userInfo.role())));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authentication;
    }

    private void setAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("인증 완료");
        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleReIssuanceOrFailure(Optional<String> refreshToken, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        refreshToken.filter(jwtUtil::isNotExpired)
                .filter(refreshTokenService::isValid)
                .map(jwtUtil::getUserInfo)
                .map(userInfo -> jwtUtil.createTokens(userInfo.id(), userInfo.role()))
                .ifPresentOrElse(tokenDto -> handleReIssuance(tokenDto, response),
                        () -> handleAuthenticationFailure(request, response, filterChain));
    }

    private void handleReIssuance(TokenDto tokenDto, HttpServletResponse response){
        log.info("token 재발급, access={}, refresh={}", tokenDto.accessToken(), tokenDto.refreshToken());
        response.setHeader(HttpHeaders.SET_COOKIE, jwtUtil.createCookie(JwtUtil.REFRESH_TYPE, tokenDto.refreshToken()));
        try {
            response.sendRedirect(jwtUtil.getRedirectUrl(tokenDto.accessToken()));
        } catch (IOException e) {
            log.info("redirect 실패");
            throw new RuntimeException(e);
        }
    }

    private void handleAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        log.info("인증 실패");
        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }

}
