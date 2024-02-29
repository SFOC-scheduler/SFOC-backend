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
        return refreshTokenRepository.findByContent(content)
                .map(RefreshToken::getIsValid)
                .orElseThrow(() -> new IllegalArgumentException("없는 refresh token"));
    }

    public void addToBlacklist(String content) {
        refreshTokenRepository.findByContent(content)
                .map(RefreshToken::addToBlacklist)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 refresh token 존재하지 않음"));
    }

}
