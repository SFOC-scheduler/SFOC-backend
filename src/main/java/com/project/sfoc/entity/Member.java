package com.project.sfoc.entity;

import com.project.sfoc.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "team_nickname")
    private String teamNickname;

    @Column(name = "user_nickname")
    private String userNickname;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "team_grant_type")
    private TeamGrant teamGrant;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

}
