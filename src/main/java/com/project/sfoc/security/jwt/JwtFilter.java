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
                .map(jwtUtil::getUserClaims)
                .map(claims -> createAuthenticationToken(claims, request))
                .ifPresentOrElse(this::setAuthentication, () -> handleReIssuanceOrFailure(refreshToken, response));

        filterChain.doFilter(request, response);
    }

    private Authentication createAuthenticationToken(UserClaims claims, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                claims,
                null,
                List.of(new SimpleGrantedAuthority(claims.role())));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authentication;
    }

    private void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("인증 완료");
    }

    private void handleReIssuanceOrFailure(Optional<String> refreshToken, HttpServletResponse response) {
        refreshToken.filter(jwtUtil::isNotExpired)
                .filter(refreshTokenService::isValid)
                .map(jwtUtil::getUserClaims)
                .map(claims -> jwtUtil.createTokens(claims.id(), claims.role()))
                .ifPresentOrElse(tokenDto -> handleReIssuance(tokenDto, response), this::handleAuthenticationFailure);
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

    private void handleAuthenticationFailure() {
        log.info("인증 실패");
    }

}
