package com.project.sfoc.entity.user;

import com.project.sfoc.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderAndSub(Provider provider, String sub);
}
