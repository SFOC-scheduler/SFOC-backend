package com.project.sfoc.team.dto;

import com.project.sfoc.team.Disclosure;

public record UpdateTeamInfo(

    String teamName,
    String description,
    Disclosure disclosure,

    String teamNickname,
    String userNickname

) {



}
