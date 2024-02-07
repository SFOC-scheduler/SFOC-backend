package com.project.sfoc.team;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByInvitationCode(String invitationCode);
}
