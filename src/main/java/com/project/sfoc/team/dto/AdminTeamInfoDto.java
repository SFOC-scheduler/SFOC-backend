package com.project.sfoc.team.dto;

import com.project.sfoc.team.Disclosure;
import lombok.Getter;

@Getter
public class AdminTeamInfoDto extends AbstractTeamInfoDto {


    public AdminTeamInfoDto(String teamName, String description, Disclosure disclosure, String teamNickname, String userNickname) {
        super(teamName, description, disclosure, teamNickname, userNickname);
    }




}
