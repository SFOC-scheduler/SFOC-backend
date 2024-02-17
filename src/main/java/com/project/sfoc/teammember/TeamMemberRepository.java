package com.project.sfoc.teammember;

import com.project.sfoc.entity.user.User;
import com.project.sfoc.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    boolean existsByTeam(Team team);

    boolean existsByUserNicknameAndTeam(String userNickname, Team team);


    List<TeamMember> findByTeam_Id(Long teamId);

    Optional<TeamMember> findByTeam_IdAndUser_Id(Long teamId, Long userId);

    void deleteByTeam_IdAndUser_Id(Long teamId, Long userId);
}
