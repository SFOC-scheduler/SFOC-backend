package com.project.sfoc.security;

import com.project.sfoc.entity.Provider;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public enum OAuthAttributes {
    GOOGLE("google", (attributes) -> {
        String email = (String) attributes.get("email");
        String sub = (String) attributes.get("sub");
        return OAuth2UserInfo.of(Provider.GOOGLE, email, sub);
    });

    private final String registrationId;
    private final Function<Map<String, Object>, OAuth2UserInfo> of;

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .of.apply(attributes);
    }
}
