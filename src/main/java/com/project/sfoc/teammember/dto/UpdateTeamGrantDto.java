package com.project.sfoc.teammember.dto;

import com.project.sfoc.teammember.TeamGrant;

public record UpdateTeamGrantDto(
        Long teamMemberId,
        TeamGrant teamGrant
) {

    public static UpdateTeamGrantDto of(Long teamMemberId, TeamGrant teamGrant) {
        return new UpdateTeamGrantDto(teamMemberId, teamGrant);
    }

}
