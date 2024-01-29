package com.project.sfoc.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubSchedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_schedule_id")
    private Long id;

    @Column(name = "is_success")
    private Boolean isSuccess;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name ="end_time")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;


}
