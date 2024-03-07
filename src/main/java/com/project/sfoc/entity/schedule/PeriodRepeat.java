package com.project.sfoc.entity.schedule;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PeriodRepeat {

    @Enumerated(EnumType.STRING)
    private PeriodType periodType;
    private List<String> periodDate;
    private RepeatType repeatType;
    private Integer repeatCount;
    private LocalDate repeatEndDate;

    public PeriodRepeat of(PeriodType periodType, List<String> periodDate, RepeatType repeatType, Integer repeatCount, LocalDate repeatEndDate) {
        return new PeriodRepeat(periodType, periodDate, repeatType, repeatCount, repeatEndDate);
    }

}
