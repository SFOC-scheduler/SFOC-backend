package com.project.sfoc.entity.entryrequest.dto;

public record RequestDeleteTeamRequestDto(

        Long teamId,
        Long userId,

        boolean apply

) {
}
