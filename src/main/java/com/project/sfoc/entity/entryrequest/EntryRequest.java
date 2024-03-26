package com.project.sfoc.entity.entryrequest;

import com.project.sfoc.entity.team.Team;
import com.project.sfoc.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EntryRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_request_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    private EntryRequest(User user, Team team) {
        this.user = user;
        this.team = team;
    }

    public static EntryRequest of(User user, Team team) {
        return new EntryRequest(user, team);
    }
}
