package com.project.sfoc.entity.teammember.dto;

import com.project.sfoc.entity.teammember.TeamGrant;

public record TeamMemberResponseDto(

        Long teamMemberId,
        String userNickname,
        TeamGrant teamGrant
) {

    public static TeamMemberResponseDto of(Long teamMemberId, String userNickname, TeamGrant teamGrant) {
        return new TeamMemberResponseDto(teamMemberId, userNickname, teamGrant);
    }

}
