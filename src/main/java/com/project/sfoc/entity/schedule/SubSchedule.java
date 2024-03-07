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

    private String title;
    private String memo;

    private Boolean isEnableDday;
    private Boolean isSuccess;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    private SubSchedule(String title, String memo, Boolean isEnableDday, Boolean isSuccess,
                        LocalDateTime startDateTime, LocalDateTime endDateTime, Schedule schedule) {
        this.title = title;
        this.memo = memo;
        this.isEnableDday = isEnableDday;
        this.isSuccess = isSuccess;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.schedule = schedule;
    }

    public static SubSchedule of(String title, String memo, Boolean isEnableDday, Boolean isSuccess,
                      LocalDateTime startDateTime, LocalDateTime endDateTime, Schedule schedule) {
        return new SubSchedule(title, memo, isEnableDday, isSuccess, startDateTime, endDateTime, schedule);
    }

}
