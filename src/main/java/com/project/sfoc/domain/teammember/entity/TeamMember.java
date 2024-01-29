package com.project.sfoc.domain.teammember.entity;

import com.project.sfoc.domain.team.entity.Team;
import com.project.sfoc.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_member_id")
    private Long id;

    @Column(name = "team_nickname")
    private String teamNickname;

    @Column(name = "user_nickname")
    private String userNickname;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "grant_type")
    private Grant grant;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

}
