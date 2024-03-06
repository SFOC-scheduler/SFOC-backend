package com.project.sfoc.team.dto;


import jakarta.validation.constraints.NotNull;

public record UserNicknameDto (

    @NotNull(message = "닉네임을 입력헤주세요")
    String userNickname
) {

}
