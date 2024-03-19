package com.project.sfoc.entity.schedule.dto;

import com.project.sfoc.entity.schedule.*;
import com.project.sfoc.entity.teammember.TeamMember;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CreateScheduleDto(
        @NotNull
        String title,
        String memo,
        @NotNull
        LocalDateTime startDateTime,
        @NotNull
        LocalDateTime endDateTime,
        @NotNull
        Boolean isEnableDday,
        PeriodType periodType,
        Long interval,
        List<LocalDate> periodDate,
        @NotNull
        RepeatType repeatType,
        Integer repeatCount,
        LocalDate repeatEndDate
) {

    public Schedule toSchedule(TeamMember teamMember) {
        return Schedule.of(title, memo, teamMember,
                PeriodRepeat.of(periodType, interval, periodDate, repeatType, repeatCount, repeatEndDate));
    }

    public List<SubSchedule> toSubSchedules(Schedule schedule) {
        return repeatType.toDateTime(RepeatType.RepeatInform.of(this))
                .map(pair -> SubSchedule.of(
                        isEnableDday,
                        Boolean.FALSE,
                        pair.getLeft(),
                        pair.getRight(),
                        schedule))
                .toList();
    }
}
