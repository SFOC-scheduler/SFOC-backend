package com.project.sfoc.security.jwt;

import com.project.sfoc.security.HttpUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class AccessTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final HttpUtil httpUtil;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("접속자 ip={}", request.getRemoteAddr());
        log.info("요청 URI={}", request.getRequestURI());

        // TODO - jwt 만료 예외 처리
        String accessToken = httpUtil.resolveAccessToken(request);
        log.info("accessToken={}", accessToken);

        UserInfo userInfo = jwtUtil.getUserInfo(accessToken);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userInfo,
                        null,
                        List.of(new SimpleGrantedAuthority(userInfo.role())));

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        log.info("인증 완료");
        filterChain.doFilter(request, response);
    }

}
