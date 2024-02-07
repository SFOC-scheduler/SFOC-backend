package com.project.sfoc.entity.user;

import com.project.sfoc.entity.Provider;

public record UserDto(
        Provider provider,
        String email,
        String sub
) {

    public static UserDto of(Provider provider, String email, String sub) {
        return new UserDto(provider, email, sub);
    }

    public static UserDto from(User user) {
        return UserDto.of(
                user.getProvider(),
                user.getEmail(),
                user.getSub()
        );
    }

    public User toEntity() {
        return User.of(provider, email, sub);
    }

}
