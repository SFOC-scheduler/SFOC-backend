package com.project.sfoc.entity;

import com.project.sfoc.team.Team;
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
}
