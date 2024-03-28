package com.project.sfoc.entity.teammember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {


    boolean existsByUserNicknameAndTeam_Id(String userNickname, Long teamId);

    boolean existsByTeam_IdAndUser_Id(Long teamId, Long userId);

    List<TeamMember> findByTeam_Id(Long teamId);

    Optional<TeamMember> findByTeam_IdAndUser_Id(Long teamId, Long userId);

    int countByTeam_Id(Long teamId);

}
