package com.project.sfoc.team;

import com.project.sfoc.entity.TeamGrant;
import com.project.sfoc.entity.TeamMember;
import com.project.sfoc.entity.user.User;

public record TeamMemberDto(

        String userNickname,
        Long userId,
        Long teamId
) {
    public TeamMember toEntity(String teamNickname, TeamGrant teamGrant, User user, Team team) {
        return TeamMember.of(teamNickname, userNickname, teamGrant, user, team);
    }

    public static TeamMemberDto of(String userNickname, Long userId, Long teamId) {
        return new TeamMemberDto(userNickname, userId, teamId);
    }

}
