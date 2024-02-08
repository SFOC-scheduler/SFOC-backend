package com.project.sfoc.security;

import com.project.sfoc.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final User user;

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("id", user.getId());
        attributes.put("sub", user.getSub());
        attributes.put("provider", user.getProvider().name());
        attributes.put("email", user.getEmail());
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getGrant().name();
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        return user.getProvider().name() + user.getSub();
    }

}
