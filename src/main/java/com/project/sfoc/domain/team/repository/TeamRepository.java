package com.project.sfoc.domain.team.repository;

import com.project.sfoc.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
