package com.project.sfoc.entity.teammember;

import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.team.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
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
    @Column(name = "team_grant_type")
    private TeamGrant teamGrant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    private TeamMember(String teamNickname, String userNickname, TeamGrant teamGrant, User user, Team team) {
        this.teamNickname = teamNickname;
        this.userNickname = userNickname;
        this.teamGrant = teamGrant;
        this.user = user;
        this.team = team;
    }

    public static TeamMember of(User user, Team team, TeamGrant teamGrant){
        return new TeamMember(team.getName(), user.getEmail(), teamGrant, user, team);
    }

    public void update(String teamNickname, String userNickname) {
            this.teamNickname = teamNickname;
            this.userNickname = userNickname;
    }

    public void updateTeamGrant(TeamGrant teamGrant) {
            this.teamGrant = teamGrant;
    }

}
