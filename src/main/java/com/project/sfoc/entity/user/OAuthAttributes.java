package com.project.sfoc.entity.user;

import com.project.sfoc.entity.Provider;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {
    GOOGLE("google", (attributes) -> {
        String email = (String) attributes.get("email");
        String sub = (String) attributes.get("sub");
        return UserDto.of(Provider.GOOGLE, email, sub);
    });

    private final String registrationId;
    private final Function<Map<String, Object>, UserDto> of;

    OAuthAttributes(String registrationId, Function<Map<String, Object>, UserDto> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static UserDto extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .of.apply(attributes);
    }
}
