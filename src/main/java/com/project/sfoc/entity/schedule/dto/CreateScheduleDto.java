package com.project.sfoc.entity.schedule.dto;

import com.project.sfoc.entity.schedule.PeriodType;
import com.project.sfoc.entity.schedule.RepeatType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CreateScheduleDto(
        Long teamMemberId,
        String title,
        String memo,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Boolean isEnableDday,
        PeriodType periodType,
        List<String> periodDate,
        RepeatType repeatType,
        Integer repeatCount,
        LocalDate repeatEndDate
) {

}
