package com.project.sfoc.entity.team.dto;

import com.project.sfoc.entity.team.Disclosure;
import lombok.Getter;

@Getter
public class AdminTeamInfoDto extends AbstractTeamInfoDto {


    public AdminTeamInfoDto(String teamName, String description, Disclosure disclosure, String teamNickname, String userNickname) {
        super(teamName, description, disclosure, teamNickname, userNickname);
    }




}
