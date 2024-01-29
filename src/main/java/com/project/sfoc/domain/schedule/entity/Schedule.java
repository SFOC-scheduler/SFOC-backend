package com.project.sfoc.domain.schedule.entity;

import com.project.sfoc.domain.teammember.entity.TeamMember;
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
    private PeriodType period_type;
    private String period;

    @Column(name = "is_dday")
    private Boolean isDDay;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name ="end_time")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "team_member_id")
    private TeamMember teamMember;

    @Column(name = "period_count")
    private Long periodCount;

    @Column(name = "period_end_time")
    private LocalDateTime periodEndTime;



}
