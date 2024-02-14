package com.project.sfoc.team;

import com.project.sfoc.entity.TeamMember;
import com.project.sfoc.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    boolean existsByTeam(Team team);

    boolean existsByUserNicknameAndTeam(String userNickname, Team team);
}
