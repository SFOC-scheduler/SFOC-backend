package com.project.sfoc.team;

public record TeamRequestDto(

    String name,
    String description,
    Disclosure disclosure
) {

    public Team toEntity(String invitationCode) {
        return Team.of(name, invitationCode, description, disclosure);
    }

}
