package com.project.sfoc.teammember;

import com.project.sfoc.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {


    boolean existsByUserNicknameAndTeam_Id(String userNickname, Long teamId);


    List<TeamMember> findByTeam_Id(Long teamId);

    Optional<TeamMember> findByTeam_IdAndUser_Id(Long teamId, Long userId);

}
