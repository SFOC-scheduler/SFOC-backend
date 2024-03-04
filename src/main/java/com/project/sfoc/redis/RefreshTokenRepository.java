package com.project.sfoc.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByContent(String content);
    boolean existsByContent(String content);
}
