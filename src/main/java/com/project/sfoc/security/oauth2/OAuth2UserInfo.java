package com.project.sfoc.security.oauth2;

import com.project.sfoc.entity.user.Provider;
import com.project.sfoc.entity.user.User;

public record OAuth2UserInfo(
        Provider provider,
        String email,
        String sub
) {

    public static OAuth2UserInfo of(Provider provider, String email, String sub) {
        return new OAuth2UserInfo(provider, email, sub);
    }

    public User toEntity() {
        return User.of(provider, email, sub);
    }

}
