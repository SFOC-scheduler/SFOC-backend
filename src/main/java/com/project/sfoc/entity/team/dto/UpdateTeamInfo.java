package com.project.sfoc.entity.team.dto;

import com.project.sfoc.entity.team.Disclosure;

public record UpdateTeamInfo(

    String teamName,
    String description,
    Disclosure disclosure,

    String teamNickname,
    String userNickname

) {



}
