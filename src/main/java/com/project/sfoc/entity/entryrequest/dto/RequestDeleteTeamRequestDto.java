package com.project.sfoc.entity.entryrequest.dto;

public record RequestDeleteTeamRequestDto(

        Long entryRequestId,
        Long teamId,
        Long userId,

        boolean apply

) {
}
