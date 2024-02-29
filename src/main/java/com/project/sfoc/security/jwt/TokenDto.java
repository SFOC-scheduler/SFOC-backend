package com.project.sfoc.security.jwt;

public record TokenDto(
    String accessToken,
    String refreshToken
) {
    public static TokenDto of(String accessToken, String refreshToken) {
        return new TokenDto(accessToken, refreshToken);
    }
}
