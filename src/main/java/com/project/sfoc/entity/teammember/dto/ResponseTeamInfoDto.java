package com.project.sfoc.entity.teammember.dto;

import com.project.sfoc.entity.team.Team;

public record ResponseTeamInfoDto(

        Long teamId,
        String teamName

) {


    public static ResponseTeamInfoDto from (Team team) {
        return new ResponseTeamInfoDto(team.getId(), team.getName());
    }
}
