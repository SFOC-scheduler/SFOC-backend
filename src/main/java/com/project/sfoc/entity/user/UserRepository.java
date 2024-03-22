package com.project.sfoc.entity.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderAndSub(Provider provider, String sub);

    @Query("select u from User u join EntryRequest er where er.team.id = :teamId")
    List<User> findRequestEntries(@Param("teamId") Long teamId);
}
