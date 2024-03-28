package com.project.sfoc.entity.teammember.dto;

import com.project.sfoc.entity.teammember.TeamGrant;

public record RequestUpdateTeamGrantDto(
        Long teamMemberId,
        TeamGrant teamGrant
) {

    public static RequestUpdateTeamGrantDto of(Long teamMemberId, TeamGrant teamGrant) {
        return new RequestUpdateTeamGrantDto(teamMemberId, teamGrant);
    }

}
