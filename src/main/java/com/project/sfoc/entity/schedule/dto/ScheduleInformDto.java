package com.project.sfoc.entity.schedule.dto;

import com.project.sfoc.entity.schedule.PeriodRepeat;
import com.project.sfoc.entity.schedule.Schedule;
import com.project.sfoc.entity.schedule.SubSchedule;

import java.time.LocalDateTime;
import java.util.List;

public record ScheduleInformDto(
        String title,
        String memo,
        PeriodRepeat periodRepeat,
        List<SubScheduleInformDto> subScheduleInforms
) {

    public static ScheduleInformDto from(Schedule schedule, List<SubSchedule> subSchedules) {
        return new ScheduleInformDto(
                schedule.getTitle(),
                schedule.getMemo(),
                schedule.getPeriodRepeat(),
                SubScheduleInformDto.from(subSchedules)
        );
    }

    public record SubScheduleInformDto(
            Boolean isEnableDday,
            Boolean isSuccess,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {

        public static SubScheduleInformDto from(SubSchedule subSchedule) {
            return new SubScheduleInformDto(
                    subSchedule.getIsEnableDday(),
                    subSchedule.getIsSuccess(),
                    subSchedule.getStartDateTime(),
                    subSchedule.getEndDateTime()
            );
        }

        public static List<SubScheduleInformDto> from(List<SubSchedule> subSchedules) {
            return subSchedules.stream()
                    .map(SubScheduleInformDto::from)
                    .toList();
        }

    }

}
