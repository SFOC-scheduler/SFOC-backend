package com.project.sfoc.team;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @Id
    @Column(name = "team_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "invitation_code")
    private String invitationCode;

    private String description;

    @Enumerated(value = EnumType.STRING)
    private Disclosure disclosure;

    private Team(String name, String invitationCode, String description, Disclosure disclosure) {
        this.name = name;
        this.invitationCode = invitationCode;
        this.description = description;
        this.disclosure = disclosure;
    }

    public static Team of(String name, String invitationCode, String description, Disclosure disclosure){
        return new Team(name, invitationCode, description, disclosure);
    }
}
