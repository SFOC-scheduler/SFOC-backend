package com.project.sfoc.security.jwt.redis;

import com.project.sfoc.security.jwt.JwtUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(timeToLive = JwtUtil.REFRESH_EXPIRED_MS)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshToken {
    @Id
    private Long userId;
    @Indexed
    private String content;

    public static RefreshToken of(Long userId, String content) {
        return new RefreshToken(userId, content);
    }
}
