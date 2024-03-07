package com.project.sfoc.entity.schedule;

import com.project.sfoc.entity.schedule.dto.CreateScheduleDto;
import com.project.sfoc.entity.schedule.dto.ScheduleInformDto;
import com.project.sfoc.entity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final SubScheduleRepository subScheduleRepository;
    private final UserRepository userRepository;

    private Schedule getScheduleBySubScheduleId(Long subScheduleId) {
        return scheduleRepository.findBySubscheduleId(subScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 entity 없음"));
    }

    private SubSchedule getSubScheduleByIdAndUserId(Long subScheduleId, Long userId) {
        return subScheduleRepository.findByIdAndUserId(subScheduleId, userId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 entity 없음"));
    }

    public void createSchedule(Long userId, CreateScheduleDto dto) {

    }

    public ScheduleInformDto getScheduleInform(Long userId, Long subScheduleId) {
        Schedule schedule = getScheduleBySubScheduleId(subScheduleId);
        SubSchedule subSchedule = getSubScheduleByIdAndUserId(subScheduleId, userId);
        long count = subScheduleRepository.countBySchedule_Id(schedule.getId());
        return ScheduleInformDto.from(schedule, subSchedule, count == 1L);
    }

}
