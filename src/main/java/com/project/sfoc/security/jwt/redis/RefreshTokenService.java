package com.project.sfoc.security.jwt.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(readOnly = true)
    public boolean isValid(String content) {
        return refreshTokenRepository.existsByContent(content);
    }

    public void deleteRefreshToken(String content) {
        refreshTokenRepository.delete(
                refreshTokenRepository.findByContent(content)
                        .orElseThrow(() -> new IllegalArgumentException("일치하는 refresh token 존재하지 않음"))
        );
    }

}
