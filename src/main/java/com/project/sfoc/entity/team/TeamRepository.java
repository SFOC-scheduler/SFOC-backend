package com.project.sfoc.entity.team;

import com.project.sfoc.entity.team.dto.ResponseTeamSearchInfoDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByInvitationCode(String invitationCode);


    @Query(value = "select t " +
            "from Team t " +
            "join t.teamMembers tm " +
            "where tm.user.id = :userId")
    List<Team> findTeams(@Param("userId") Long userId);

    // on절 사용하는 것이 필터링이 돼서 더 성능이 향상 될 것 같다.
    @Query("select new com.project.sfoc.entity.team.dto.ResponseTeamSearchInfoDto(t.id, t.name, t.description, u.email) " +
            "from Team t " +
            "join t.teamMembers tm join tm.user u " +
            "on tm.teamGrant = 'HIGHEST_ADMIN' " +
            "where lower(team.name) like lower('%' || :searchTeam || '%')")
    Page<ResponseTeamSearchInfoDto> findSearchResult(@Param("searchTeam") String searchTeam, Pageable pageable);

}
