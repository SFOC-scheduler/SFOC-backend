package com.project.sfoc.domain.user.repository;

import com.project.sfoc.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
