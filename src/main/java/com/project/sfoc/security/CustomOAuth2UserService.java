package com.project.sfoc.security;

import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.user.UserDto;
import com.project.sfoc.entity.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest
                .getClientRegistration()
                .getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes(); // OAuth 서비스의 유저 정보들

        UserDto userDto = OAuthAttributes.extract(registrationId, attributes);
        User user = userService.findByProviderAndSub(userDto.provider(), userDto.sub())
                .map(u -> u.update(userDto.email())) // OAuth 서비스 사이트에서 유저 정보 변경이 있을 수 있기 때문에 우리 DB에도 update
                .orElse(userDto.toEntity());
        userService.saveUser(user);

        return new CustomOAuth2User(user);
    }
}
