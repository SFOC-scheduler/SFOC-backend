package com.project.sfoc.domain.team.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
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

}
