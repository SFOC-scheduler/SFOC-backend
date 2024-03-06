package com.project.sfoc.entity.team;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByInvitationCode(String invitationCode);
}
