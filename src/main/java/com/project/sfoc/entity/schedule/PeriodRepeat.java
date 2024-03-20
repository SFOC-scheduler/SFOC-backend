package com.project.sfoc.entity.schedule;

import java.time.LocalDate;
import java.util.List;

public record PeriodRepeat(
    PeriodType periodType,
    Long interval,
    List<LocalDate> periodDate,
    RepeatType repeatType,
    Integer repeatCount,
    LocalDate repeatEndDate
) {

    public static PeriodRepeat of(PeriodType periodType, Long interval, List<LocalDate> periodDate, RepeatType repeatType, Integer repeatCount, LocalDate repeatEndDate) {
        return new PeriodRepeat(periodType, interval, periodDate, repeatType, repeatCount, repeatEndDate);
    }

}
