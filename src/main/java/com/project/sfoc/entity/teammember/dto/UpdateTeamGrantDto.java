package com.project.sfoc.entity.teammember.dto;

import com.project.sfoc.entity.teammember.TeamGrant;

public record UpdateTeamGrantDto(
        Long teamMemberId,
        TeamGrant teamGrant
) {

    public static UpdateTeamGrantDto of(Long teamMemberId, TeamGrant teamGrant) {
        return new UpdateTeamGrantDto(teamMemberId, teamGrant);
    }

}
