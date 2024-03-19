package com.project.sfoc.entity.team.dto;

import com.project.sfoc.entity.team.Team;


public record ResponseTeamSearchInfoDto(

        Long teamId,
        String teamName,

        String description,

        String email

) {

    public static ResponseTeamSearchInfoDto from(Team team, String email) {
        return new ResponseTeamSearchInfoDto(team.getId(), team.getName(), team.getDescription(), email);
    }

}
