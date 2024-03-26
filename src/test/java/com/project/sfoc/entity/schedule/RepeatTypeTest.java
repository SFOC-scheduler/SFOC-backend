package com.project.sfoc.entity.schedule;

import com.nimbusds.jose.util.Pair;
import com.project.sfoc.entity.schedule.dto.CreateScheduleDto;
import com.project.sfoc.entity.team.Disclosure;
import com.project.sfoc.entity.team.Team;
import com.project.sfoc.entity.teammember.TeamGrant;
import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.user.Provider;
import com.project.sfoc.entity.user.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class RepeatTypeTest {

    private User getUser() {
        User user = User.of(Provider.GOOGLE, "이메일", "식별코드");
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private Team getTeam() {
        Team team = Team.of("팀 이름", "팀 초대코드", "팀 설명", Disclosure.PUBLIC);
        ReflectionTestUtils.setField(team, "id", 2L);
        return team;
    }

    private TeamMember getTeamMember() {
        TeamMember teamMember = TeamMember.of(getUser(), getTeam(), TeamGrant.NORMAL);
        ReflectionTestUtils.setField(teamMember, "id", 3L);
        return teamMember;
    }

    private CreateScheduleDto getCreateScheduleDto(Boolean isEnableDday,
                                                   PeriodType periodType, Long interval, List<LocalDate> periodDate,
                                                   RepeatType repeatType, Integer repeatCount, LocalDate repeatEndDate) {
        return new CreateScheduleDto(
                "제목",
                "설명",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2L),
                isEnableDday,
                periodType,
                interval,
                periodDate,
                repeatType,
                repeatCount,
                repeatEndDate
        );
    }

    @Test
    @DisplayName("반복 없음")
    void noneToDateTimeTest() {
        // given
        CreateScheduleDto dto = getCreateScheduleDto(Boolean.FALSE, null, null, List.of(LocalDate.now()), RepeatType.NONE, null, null);
        RepeatType.RepeatInform repeatInform = RepeatType.RepeatInform.of(dto);

        // when
        Stream<Pair<LocalDateTime, LocalDateTime>> dateTimes = dto.repeatType().toDateTime(repeatInform);

        // then
        assertThat(dateTimes.filter(datetime ->
                        datetime.getLeft().isEqual(dto.startDateTime()) &&
                        datetime.getRight().isEqual(dto.endDateTime())
        ).count()).isEqualTo(dto.periodDate().size());
    }

    @Test
    @DisplayName("특정 횟수 반복")
    void countToDateTimeTest() {
        // given
        CreateScheduleDto dto = getCreateScheduleDto(Boolean.FALSE, PeriodType.DAY, 2L, List.of(LocalDate.now()), RepeatType.COUNT, 5, null);
        RepeatType.RepeatInform repeatInform = RepeatType.RepeatInform.of(dto);

        // when
        List<Pair<LocalDateTime, LocalDateTime>> list = dto.repeatType().toDateTime(repeatInform).toList();

        // then
        // 각 start와 end의 차이가 between인가?
        // List 원소 수가 repeatCount와 같은가?
        Duration duration = Duration.between(dto.startDateTime(), dto.endDateTime());
        assertThat(list.stream().filter(datetime -> {
                    boolean equal = datetime.getRight().isEqual(datetime.getLeft().plus(duration));
                    log.info("left={}, right={}, duration={}", datetime.getLeft(), datetime.getRight(), duration);
                    return equal;
                })
                .count())
                .isEqualTo((int)repeatInform.repeatCount());

        // 각 start의 차이가 interval인가?
        List<LocalDateTime> dates = list.stream()
                .map(Pair::getLeft)
                .toList();
        log.info("dates={}", dates);
        assertTrue(IntStream.range(1, list.size())
                .mapToObj(i -> Pair.of(dates.get(i - 1), dates.get(i)))
                .allMatch(p -> Duration.between(p.getLeft(), p.getRight()).equals(Duration.ofDays(repeatInform.interval()))));
    }

    @Test
    @DisplayName("특정 날짜까지 반복")
    void endDateToDateTimeTest() {
        // given
        CreateScheduleDto dto = getCreateScheduleDto(Boolean.FALSE, PeriodType.DAY, 2L, List.of(LocalDate.now()), RepeatType.END_DATE, null, LocalDate.now().plusDays(10));
        RepeatType.RepeatInform repeatInform = RepeatType.RepeatInform.of(dto);

        // when
        List<Pair<LocalDateTime, LocalDateTime>> list = dto.repeatType().toDateTime(repeatInform).toList();

        // then
        // 각 start와 end의 차이가 between인가?
        // List 원소가 repeatEndDate까지인가?
        Duration duration = Duration.between(dto.startDateTime(), dto.endDateTime());
        Duration totalDuration = Duration.between(
                repeatInform.startDateTime(),
                repeatInform.repeatEndDate().atTime(LocalTime.MAX).minus(duration));
        assertThat(list.stream().filter(datetime -> {
                    boolean equal = datetime.getRight().isEqual(datetime.getLeft().plus(duration));
                    log.info("left={}, right={}, duration={}", datetime.getLeft(), datetime.getRight(), duration);
                    return equal;
                })
                .count())
                .isEqualTo(totalDuration.dividedBy(repeatInform.interval()).toDays());

        // 각 start의 차이가 interval인가?
        List<LocalDateTime> dates = list.stream()
                .map(Pair::getLeft)
                .toList();
        log.info("dates={}", dates);
        assertTrue(IntStream.range(1, list.size())
                .mapToObj(i -> Pair.of(dates.get(i - 1), dates.get(i)))
                .allMatch(p -> Duration.between(p.getLeft(), p.getRight()).equals(Duration.ofDays(repeatInform.interval()))));
    }

}