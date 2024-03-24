package com.project.sfoc.entity.schedule;

import com.project.sfoc.entity.schedule.dto.CreateScheduleDto;
import com.project.sfoc.entity.schedule.dto.ScheduleInformDto;
import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.teammember.TeamMemberRepository;
import com.project.sfoc.exception.EntityNotFoundException;
import com.project.sfoc.exception.Error;
import com.project.sfoc.exception.IllegalDtoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final SubScheduleRepository subScheduleRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void createSchedule(Long userId, Long teamId, CreateScheduleDto dto) {
        log.info("dto={}", dto);

        if (Duration.between(dto.startDateTime(), dto.endDateTime()).toSeconds() >= dto.periodType().secondsByType(dto.interval()))
            throw new IllegalDtoException("일정 반복 주기는 일정보다 길게 설정해야 합니다.", Error.INVALID_DTO);

        TeamMember teamMember = teamMemberRepository.findByTeam_IdAndUser_Id(teamId, userId)
                .orElseThrow(() -> new EntityNotFoundException(Error.INVALID_DTO));
        Schedule schedule = dto.toSchedule(teamMember);
        List<SubSchedule> subSchedules = dto.toSubSchedules(schedule);

        scheduleRepository.save(schedule);
        subScheduleRepository.saveAll(subSchedules);
    }

    // TODO - Schedule 개수만큼 N+1 문제 발생
    public List<ScheduleInformDto> getTeamSchedules(Long teamId, Long userId) {
        if (!teamMemberRepository.existsByTeam_IdAndUser_Id(teamId, userId))
            throw new EntityNotFoundException(Error.DENIED_ACCESS);

        return scheduleRepository.findAllByTeamMember_Team_Id(teamId).stream()
                .map(schedule -> ScheduleInformDto.from(
                        schedule, subScheduleRepository.findAllBySchedule_Id(schedule.getId())))
                .toList();
    }

    public List<ScheduleInformDto> getAllSchedules(Long userId) {
        return scheduleRepository.findAllByTeamMember_User_Id(userId).stream()
                .map(schedule -> ScheduleInformDto.from(
                        schedule, subScheduleRepository.findAllBySchedule_Id(schedule.getId())))
                .toList();
    }
}
