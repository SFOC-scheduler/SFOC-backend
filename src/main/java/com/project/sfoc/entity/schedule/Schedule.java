package com.project.sfoc.entity.schedule;

import com.project.sfoc.entity.teammember.TeamMember;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    private String title;
    private String memo;

    @Enumerated(EnumType.STRING)
    private PeriodType periodType;
    private String period;

    private Boolean isEnableDday;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "team_member_id")
    private TeamMember teamMember;

    private Long periodCount;

    private LocalDateTime periodEndTime;

}
