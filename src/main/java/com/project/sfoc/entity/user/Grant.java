package com.project.sfoc.entity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Grant {
    ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

    private final String role;
}
