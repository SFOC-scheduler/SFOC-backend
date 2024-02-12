package com.project.sfoc.security;

import com.project.sfoc.security.jwt.UserClaims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @GetMapping("/loginInfo")
    public ResponseEntity<Map<String, Object>> oauthLoginInfo(Authentication authentication){
        UserClaims userClaims = (UserClaims) authentication.getPrincipal();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("Anonymous");

        Map<String, Object> map = new HashMap<>();
        map.put("id", userClaims.id());
        map.put("role", role);

        return ResponseEntity.ok(map);
    }

}
