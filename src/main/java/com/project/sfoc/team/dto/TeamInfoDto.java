package com.project.sfoc.team.dto;

import com.project.sfoc.teammember.TeamGrant;
import com.project.sfoc.team.Disclosure;
import com.project.sfoc.team.Team;
import com.project.sfoc.teammember.TeamMember;

public record TeamInfoDto(
        String teamName,
        String description,
        Disclosure disclosure,

        String teamNickname,

        String userNickname,

        TeamGrant teamGrant
) {


    public static TeamInfoDto from(TeamMember teamMember, Team team) {
        return new TeamInfoDto(
                team.getName(), team.getDescription(), team.getDisclosure(),
                teamMember.getTeamNickname(), teamMember.getUserNickname(), teamMember.getTeamGrant());
    }



}
