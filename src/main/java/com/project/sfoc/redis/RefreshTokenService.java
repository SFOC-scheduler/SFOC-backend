package com.project.sfoc.redis;

import com.project.sfoc.exception.Error;
import com.project.sfoc.exception.RefreshTokenException;
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
                        .orElseThrow(() -> new RefreshTokenException(Error.INVALID_TOKEN))
        );
    }

}
