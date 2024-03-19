package com.project.sfoc.entity.team.dto;

public record RequestTeamSearchDto(
        String teamSearch
) {

    public static RequestTeamSearchDto of(String search) {
        return new RequestTeamSearchDto(search);
    }
}
