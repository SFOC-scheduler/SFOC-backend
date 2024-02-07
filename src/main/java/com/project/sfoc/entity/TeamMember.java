package com.project.sfoc.entity;

import com.project.sfoc.team.Disclosure;
import com.project.sfoc.team.Team;
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

    public TeamMember(String teamNickname, String userNickname, Grant grant, User user, Team team) {
        this.teamNickname = teamNickname;
        this.userNickname = userNickname;
        this.grant = grant;
        this.user = user;
        this.team = team;
    }

    public static TeamMember of(String teamNickname, String userNickname,
                                Grant grant, User user, Team team){
        return new TeamMember(teamNickname, userNickname, grant, user, team);
    }

}
