package com.project.sfoc.security.jwt;

import java.util.HashMap;
import java.util.Map;

public record UserInfo(
        Long id,
        String role
) {

    public static UserInfo of(Long id, String role) {
        return new UserInfo(id, role);
    }

    public Map<String, Object> toPrincipal() {
        HashMap<String, Object> principal = new HashMap<>();
        principal.put("id", id);
        return principal;
    }
}
