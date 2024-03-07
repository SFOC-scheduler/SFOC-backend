package com.project.sfoc.entity.schedule;

import com.project.sfoc.entity.schedule.dto.CreateScheduleDto;
import com.project.sfoc.entity.schedule.dto.ScheduleInformDto;
import com.project.sfoc.security.jwt.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/")
    public ResponseEntity<Void> creatSchedule(@AuthenticationPrincipal UserInfo userInfo, CreateScheduleDto dto) {
        scheduleService.createSchedule(userInfo.id(), dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/schedules/{subScheduleId}")
    public ResponseEntity<ScheduleInformDto> getScheduleInform(@AuthenticationPrincipal UserInfo userInfo,
                                                               @PathVariable Long subScheduleId) {
        ScheduleInformDto dto = scheduleService.getScheduleInform(userInfo.id(), subScheduleId);
        return ResponseEntity.ok().body(dto);
    }

}
