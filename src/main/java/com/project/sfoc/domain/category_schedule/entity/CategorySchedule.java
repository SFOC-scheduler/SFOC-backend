package com.project.sfoc.domain.category_schedule.entity;

import com.project.sfoc.domain.category.entity.Category;
import com.project.sfoc.domain.schedule.entity.Schedule;
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
