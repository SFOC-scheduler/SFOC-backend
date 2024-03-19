package com.project.sfoc.entity.schedule;

import com.project.sfoc.entity.schedule.dto.CreateScheduleDto;
import com.project.sfoc.entity.team.Disclosure;
import com.project.sfoc.entity.team.Team;
import com.project.sfoc.entity.teammember.TeamGrant;
import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.teammember.TeamMemberRepository;
import com.project.sfoc.entity.user.Provider;
import com.project.sfoc.entity.user.User;
import com.project.sfoc.exception.EntityNotFoundException;
import com.project.sfoc.exception.Error;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private SubScheduleRepository subScheduleRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private ScheduleService scheduleService;

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
        TeamMember teamMember = TeamMember.of("팀 닉네임", "닉네임", TeamGrant.NORMAL, getUser(), getTeam());
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
    @DisplayName("일정 생성 실패 - 멤버와 유저가 다름")
    void createScheduleFailure() {
        // given
        TeamMember teamMember = getTeamMember();
        Team team = getTeam();
        User user = getUser();
        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId()))
                .willReturn(Optional.empty());

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> scheduleService.createSchedule(
                        user.getId(), team.getId(), getCreateScheduleDto(
                                Boolean.FALSE, PeriodType.DAY, 2L,
                                List.of(LocalDate.now()), RepeatType.COUNT, 5, null)));

        // then
        assertEquals(exception.getError(), Error.INVALID_DTO);
    }

    @Test
    @DisplayName("일정 생성 성공")
    void createScheduleSuccess() {
        // given
        TeamMember teamMember = getTeamMember();
        Team team = getTeam();
        User user = getUser();
        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId()))
                .willReturn(Optional.of(teamMember));

        CreateScheduleDto dto = getCreateScheduleDto(
                Boolean.FALSE, PeriodType.DAY, 2L,
                List.of(LocalDate.now()), RepeatType.COUNT, 5, null);

        // when
        scheduleService.createSchedule(user.getId(), team.getId(), dto);

        // then
        then(scheduleRepository).should().save(any(Schedule.class));
        then(subScheduleRepository).should().saveAll(anyList());
    }

    
}