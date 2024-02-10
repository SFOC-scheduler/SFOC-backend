package com.project.sfoc.security;

import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oauth2UserInfo = OAuthAttributes.of(registrationId, super.loadUser(userRequest).getAttributes());
        User user = saveOrUpdateUser(oauth2UserInfo);

        return new CustomOAuth2User(user);
    }

    private User saveOrUpdateUser(OAuth2UserInfo oauth2UserInfo) {
        return userRepository.findByProviderAndSub(oauth2UserInfo.provider(), oauth2UserInfo.sub())
                .map(user -> user.update(oauth2UserInfo.email())) // OAuth 서비스 사이트에서 유저 정보 변경이 있을 수 있기 때문에 우리 DB에도 update
                .orElseGet(() -> userRepository.save(oauth2UserInfo.toEntity()));
    }
}
