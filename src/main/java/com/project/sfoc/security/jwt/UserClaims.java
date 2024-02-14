package com.project.sfoc.security.jwt;

import java.util.HashMap;
import java.util.Map;

public record UserClaims(
        Long id,
        String role
) {

    public static UserClaims of(Long id, String role) {
        return new UserClaims(id, role);
    }

    public Map<String, Object> toPrincipal() {
        HashMap<String, Object> principal = new HashMap<>();
        principal.put("id", id);
        return principal;
    }
}
