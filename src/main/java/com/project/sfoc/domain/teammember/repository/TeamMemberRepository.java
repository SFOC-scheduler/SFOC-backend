package com.project.sfoc.domain.teammember.repository;

import com.project.sfoc.domain.teammember.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
}
