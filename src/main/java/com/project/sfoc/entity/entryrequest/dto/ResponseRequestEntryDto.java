package com.project.sfoc.entity.entryrequest.dto;

import com.project.sfoc.entity.user.User;

public record ResponseRequestEntryDto(


        Long userId,

        Long teamId,

        String email
) {

    public static ResponseRequestEntryDto from(User user, Long teamId) {
        return new ResponseRequestEntryDto(user.getId(), teamId ,user.getEmail());
    }
}
