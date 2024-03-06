package com.project.sfoc.entity.team.dto;

import com.project.sfoc.entity.team.Team;
import com.project.sfoc.entity.teammember.TeamGrant;
import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.teammember.TeamMember;

public record TeamMemberDto(

        Long userId,
        Long teamId,
        String userNickname,
        TeamGrant teamGrant
) {
    public TeamMember toEntity(String teamNickname, User user, Team team) {
        return TeamMember.of(team.getName(), userNickname, teamGrant, user, team);
    }

    public static TeamMemberDto of(Long userId, Long teamId, String userNickname, TeamGrant teamGrant) {
        return new TeamMemberDto(userId, teamId, userNickname, teamGrant);
    }


    public static TeamMemberDto from(TeamMember teamMember) {
        return TeamMemberDto.of(
            teamMember.getUser().getId(),
            teamMember.getTeam().getId(),
            teamMember.getUserNickname(),
            teamMember.getTeamGrant()
        );
    }

}
