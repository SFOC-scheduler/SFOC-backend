package com.project.sfoc.entity.team.dto;

import lombok.Getter;

@Getter
public class NormalTeamInfoDto extends AbstractTeamInfoDto{

    public NormalTeamInfoDto(String teamNickname, String userNickname) {
        super(teamNickname, userNickname);
    }

}
