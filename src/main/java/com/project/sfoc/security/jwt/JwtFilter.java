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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

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

        String accessToken = jwtUtil.resolveAccessToken(request).orElse(null);
        log.info("accessToken={}", accessToken);

        if (accessToken == null) {
            log.info("인증 실패");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            UserClaims userClaims = jwtUtil.getUserClaims(accessToken);
            log.info("인증 완료");

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userClaims,
                            null,
                            List.of(new SimpleGrantedAuthority(userClaims.role())));

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e1) {
            String refreshToken = jwtUtil.resolveRefreshToken(request).orElse(null);
            log.info("refreshToken={}", refreshToken);

            if (refreshToken != null && refreshTokenService.isValid(refreshToken)) {
                try {
                    UserClaims userClaims = jwtUtil.getUserClaims(refreshToken);
                    TokenDto tokenDto = jwtUtil.createTokens(userClaims.id(), userClaims.role());
                    log.info("access token 재발급");

                    response.setHeader(HttpHeaders.SET_COOKIE, jwtUtil.createCookie(JwtUtil.REFRESH_TYPE, tokenDto.refreshToken()));
                    response.sendRedirect(jwtUtil.getRedirectUrl(tokenDto.accessToken()));
                } catch (Exception e2) {
                    log.info("유효하지 않은 refresh token");
                }
            }
        } finally {
            filterChain.doFilter(request, response);
        }
    }

}
