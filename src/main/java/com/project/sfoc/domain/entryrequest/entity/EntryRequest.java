package com.project.sfoc.domain.entryrequest.entity;

import com.project.sfoc.domain.team.entity.Team;
import com.project.sfoc.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EntryRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "addmission_request_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
}
