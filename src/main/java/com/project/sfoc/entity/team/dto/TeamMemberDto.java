package com.project.sfoc.entity.team.dto;

import com.project.sfoc.entity.teammember.TeamGrant;
import com.project.sfoc.entity.teammember.TeamMember;

public record TeamMemberDto(

        Long userId,
        Long teamId,
        TeamGrant teamGrant
) {
    public static TeamMemberDto of(Long userId, Long teamId, TeamGrant teamGrant) {
        return new TeamMemberDto(userId, teamId, teamGrant);
    }


    public static TeamMemberDto from(TeamMember teamMember) {
        return TeamMemberDto.of(
            teamMember.getUser().getId(),
            teamMember.getTeam().getId(),
            teamMember.getTeamGrant()
        );
    }

}
