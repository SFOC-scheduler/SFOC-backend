package com.project.sfoc.entity;

import jakarta.persistence.*;

@Entity
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
