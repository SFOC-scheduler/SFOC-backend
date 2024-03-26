package com.project.sfoc.entity.schedule;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubSchedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_schedule_id")
    private Long id;

    private Boolean isEnableDday;
    private Boolean isSuccess;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    private SubSchedule(Boolean isEnableDday, Boolean isSuccess,
                        LocalDateTime startDateTime, LocalDateTime endDateTime, Schedule schedule) {
        this.isEnableDday = isEnableDday;
        this.isSuccess = isSuccess;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.schedule = schedule;
    }

    public static SubSchedule of(Boolean isEnableDday, Boolean isSuccess,
                      LocalDateTime startDateTime, LocalDateTime endDateTime, Schedule schedule) {
        return new SubSchedule(isEnableDday, isSuccess, startDateTime, endDateTime, schedule);
    }

}
