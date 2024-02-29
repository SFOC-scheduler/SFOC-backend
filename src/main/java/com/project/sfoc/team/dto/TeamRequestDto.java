package com.project.sfoc.team.dto;

import com.project.sfoc.team.Disclosure;
import com.project.sfoc.team.Team;

public record TeamRequestDto(

    String name,
    String description,
    Disclosure disclosure,
    String userNickname
) {

    public Team toEntity(String invitationCode) {
        return Team.of(name, invitationCode, description, disclosure);
    }


}
