package com.project.sfoc.entity.teammember.dto;

import com.project.sfoc.entity.teammember.TeamGrant;

public record ResponseTeamMemberDto(

        Long teamMemberId,
        String userNickname,
        TeamGrant teamGrant
) {

    public static ResponseTeamMemberDto of(Long teamMemberId, String userNickname, TeamGrant teamGrant) {
        return new ResponseTeamMemberDto(teamMemberId, userNickname, teamGrant);
    }

}
