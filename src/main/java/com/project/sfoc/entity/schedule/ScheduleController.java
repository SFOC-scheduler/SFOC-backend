package com.project.sfoc.entity.schedule;

import com.project.sfoc.entity.schedule.dto.CreateScheduleDto;
import com.project.sfoc.security.jwt.UserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams/{teamId}/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<Void> creatSchedule(@AuthenticationPrincipal UserInfo userInfo,
                                              @PathVariable Long teamId,
                                              @RequestBody @Valid CreateScheduleDto dto) {
        scheduleService.createSchedule(userInfo.id(), teamId, dto);
        return ResponseEntity.ok().build();
    }

}
