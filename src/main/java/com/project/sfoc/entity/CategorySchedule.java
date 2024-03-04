package com.project.sfoc.entity;

import com.project.sfoc.entity.schedule.Schedule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategorySchedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_schedule_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

}
