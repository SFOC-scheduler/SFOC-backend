package com.project.sfoc.entity.user;

import com.project.sfoc.entity.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> findByProviderAndSub(Provider provider, String sub) {
        return userRepository.findByProviderAndSub(provider, sub);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

}
