package com.project.sfoc.entity.team.dto;

import com.project.sfoc.entity.team.Disclosure;
import com.project.sfoc.entity.team.Team;
import com.project.sfoc.global.vaild.ValidEnum;
import jakarta.validation.constraints.NotNull;

public record RequestTeamDto(
    @NotNull(message = "팀 이름을 입력해주세요")
    String name,
    @NotNull(message = "팀 설명을 적어주세요")
    String description,
    @ValidEnum(enumClass = Disclosure.class, message = "팀 권한을 선택해주세요")
    Disclosure disclosure,
    @NotNull(message = "팀 닉네임을 적어주세요")
    String userNickname
) {

    public Team toEntity(String invitationCode) {
        return Team.of(name, invitationCode, description, disclosure);
    }


}
