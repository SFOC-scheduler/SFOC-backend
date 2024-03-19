package com.project.sfoc.entity.team;

import com.project.sfoc.entity.teammember.TeamMember;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @Id
    @Column(name = "team_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "invitation_code", unique = true)
    private String invitationCode;

    private String description;

    @Enumerated(value = EnumType.STRING)
    private Disclosure disclosure;

    @OneToMany(mappedBy = "team")
    private List<TeamMember> teamMembers = new ArrayList<>();

    private Team(String name, String invitationCode, String description, Disclosure disclosure) {
        this.name = name;
        this.invitationCode = invitationCode;
        this.description = description;
        this.disclosure = disclosure;
    }

    public static Team of(String name, String invitationCode, String description, Disclosure disclosure){
        return new Team(name, invitationCode, description, disclosure);
    }

    public void update(String name, String description ,Disclosure disclosure) {
            this.name = name;
            this.description = description;
            this.disclosure = disclosure;
    }
}
