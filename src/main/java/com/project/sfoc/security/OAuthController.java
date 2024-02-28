package com.project.sfoc.security;

import com.project.sfoc.security.jwt.UserClaims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @GetMapping("/loginInfo")
    public ResponseEntity<UserClaims> oauthLoginInfo(Authentication authentication) {
        try {
            UserClaims userClaims = (UserClaims) authentication.getPrincipal();
            return ResponseEntity.ok(userClaims);
        } catch (Exception e) {
            return ResponseEntity.ok(UserClaims.of(null, "anonymous"));
        }
    }

}
