package com.project.sfoc.security.oauth2;

import com.project.sfoc.entity.user.Provider;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public enum OAuthAttributes {

    GOOGLE("google", (attributes) -> {
        String email = String.valueOf( attributes.get("email"));
        String sub = String.valueOf(attributes.get("sub"));
        return OAuth2UserInfo.of(Provider.GOOGLE, email, sub);
    }),

    KAKAO("kakao", (attributes) -> {
//        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
//        String email = String.valueOf(kakaoAccount.get("email"));
        String id = String.valueOf(attributes.get("id"));
        return OAuth2UserInfo.of(Provider.KAKAO, null, id);  //TODO - 이메일 정보 권한 받아오기
    });

    private final String registrationId;
    private final Function<Map<String, Object>, OAuth2UserInfo> of;

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 provider 없음"))
                .of.apply(attributes);
    }

}
