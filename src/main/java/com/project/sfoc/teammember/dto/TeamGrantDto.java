package com.project.sfoc.teammember.dto;

import com.project.sfoc.teammember.TeamGrant;

public record TeamGrantDto(
        Long teamMemberId,
        TeamGrant teamGrant
) {

    public static TeamGrantDto of(Long teamMemberId, TeamGrant teamGrant) {
        return new TeamGrantDto(teamMemberId, teamGrant);
    }

}
