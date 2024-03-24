package com.project.sfoc.entity.schedule;

import com.project.sfoc.entity.schedule.dto.CreateScheduleDto;
import com.project.sfoc.entity.schedule.dto.ScheduleInformDto;
import com.project.sfoc.security.jwt.UserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/teams/{teamId}/schedules")
    public ResponseEntity<Void> creatSchedule(@AuthenticationPrincipal UserInfo userInfo,
                                              @PathVariable Long teamId,
                                              @RequestBody @Valid CreateScheduleDto dto) {
        scheduleService.createSchedule(userInfo.id(), teamId, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/teams/{teamId}/schedules")
    public ResponseEntity<List<ScheduleInformDto>> getTeamSchedules(@AuthenticationPrincipal UserInfo userInfo,
                                                                    @PathVariable Long teamId) {
        List<ScheduleInformDto> schedules = scheduleService.getTeamSchedules(teamId, userInfo.id());
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleInformDto>> getAllSchedules(@AuthenticationPrincipal UserInfo userInfo) {
        List<ScheduleInformDto> schedules = scheduleService.getAllSchedules(userInfo.id());
        return ResponseEntity.ok(schedules);
    }

}
