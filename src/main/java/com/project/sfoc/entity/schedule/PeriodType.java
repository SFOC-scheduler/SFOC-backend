package com.project.sfoc.entity.schedule;

import lombok.RequiredArgsConstructor;

import java.time.*;
import java.util.function.BiFunction;
import java.util.function.Function;

@RequiredArgsConstructor
public enum PeriodType {
    DAY(LocalDate::plusDays, interval -> (Period.ofDays(interval).getDays())),
    WEEK((date, interval) -> date.plusDays(interval * 7), interval -> Period.ofDays(7 * interval).getDays()),
    MONTH(LocalDate::plusMonths, interval -> Period.ofWeeks(interval).getDays()),
    YEAR(LocalDate::plusYears, interval -> Period.ofYears(interval).getDays());
    
    private final BiFunction<LocalDate, Long, LocalDate> plusByType;
    private final Function<Integer, Integer> daysByType;

    public LocalDate plusByType(LocalDate date, Long interval) {
        return this.plusByType.apply(date, interval);
    }
    public long secondsByType(Long interval) {
        return Duration.ofDays(this.daysByType.apply(interval.intValue())).toSeconds();
    }
}
