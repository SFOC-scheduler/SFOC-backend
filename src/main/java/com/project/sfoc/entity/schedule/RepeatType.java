package com.project.sfoc.entity.schedule;

import com.nimbusds.jose.util.Pair;
import com.project.sfoc.entity.schedule.dto.CreateScheduleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public enum RepeatType {
    NONE(repeatInform -> Stream.of(Pair.of(repeatInform.startDateTime, repeatInform.endDateTime))),
    COUNT(repeatInform -> {
        Duration duration = Duration.between(repeatInform.startDateTime, repeatInform.endDateTime);
        return repeatInform.periodDate.stream()
                .flatMap(date -> IntStream.range(0, repeatInform.repeatCount)
                        .mapToObj(count -> {
                            LocalDateTime startDateTime = LocalDateTime.of(
                                    repeatInform.periodType.plusByType(date, count * repeatInform.interval),
                                    repeatInform.startDateTime.toLocalTime());
                            return Pair.of(startDateTime, startDateTime.plus(duration));
                        }));
    }), END_DATE(repeatInform -> {
        Duration duration = Duration.between(repeatInform.startDateTime, repeatInform.endDateTime);
        Duration totalDuration = Duration.between(
                repeatInform.startDateTime,
                repeatInform.repeatEndDate().atTime(LocalTime.MAX).minus(duration));

        return repeatInform.periodDate.stream()
                .flatMap(date -> Stream.iterate(date, nextDate -> repeatInform.periodType.plusByType(nextDate, repeatInform.interval))
                        .limit(totalDuration.dividedBy(repeatInform.interval).toDays())
                        .map(repeatedDate -> {
                            LocalDateTime startDateTime = LocalDateTime.of(
                                    repeatedDate,
                                    repeatInform.startDateTime.toLocalTime());
                            return Pair.of(startDateTime, startDateTime.plus(duration));
                        }));
    });

    private final Function<RepeatInform, Stream<Pair<LocalDateTime, LocalDateTime>>> toDateTime;

    public Stream<Pair<LocalDateTime, LocalDateTime>> toDateTime(RepeatInform repeatInform) {
        return this.toDateTime.apply(repeatInform);
    }

    public record RepeatInform(
            PeriodType periodType,
            List<LocalDate> periodDate,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Long interval,
            Integer repeatCount,
            LocalDate repeatEndDate
    ) {
        public static RepeatInform of(CreateScheduleDto dto) {
            return new RepeatInform(
                    dto.periodType(),
                    dto.periodDate(),
                    dto.startDateTime(),
                    dto.endDateTime(),
                    dto.interval(),
                    dto.repeatCount(),
                    dto.repeatEndDate()
            );
        }
    }

}
