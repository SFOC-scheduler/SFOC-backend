package com.project.sfoc.entity.schedule.dto;

import com.project.sfoc.entity.schedule.PeriodType;
import com.project.sfoc.entity.schedule.Schedule;
import com.project.sfoc.entity.schedule.SubSchedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ScheduleInformDto(
        String title,
        String memo,
        PeriodType periodType,
        List<LocalDate> period,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Boolean isEnableDday,
        Integer periodCount,
        Boolean isSingle
) {

    public static ScheduleInformDto from(Schedule schedule, SubSchedule subSchedule, Boolean isSingle) {
        return new ScheduleInformDto(
                schedule.getTitle(),
                schedule.getMemo(),
                schedule.getPeriodRepeat().periodType(),
                schedule.getPeriodRepeat().periodDate(),
                subSchedule.getStartDateTime(),
                subSchedule.getEndDateTime(),
                subSchedule.getIsEnableDday(),
                schedule.getPeriodRepeat().repeatCount(),
                isSingle
        );
    }

}
