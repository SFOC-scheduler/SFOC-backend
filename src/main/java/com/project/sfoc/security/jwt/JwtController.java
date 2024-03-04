package com.project.sfoc.security.jwt;

import com.project.sfoc.security.jwt.UserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/oauth")
public class OAuthController {
public class JwtController {

    @GetMapping("/loginInfo")
    public ResponseEntity<UserInfo> oauthLoginInfo(@AuthenticationPrincipal UserInfo userInfo) {
        return ResponseEntity.ok(
                Optional.ofNullable(userInfo)
                        .orElse(UserInfo.of(null, "anonymous"))
        );
    }

}
