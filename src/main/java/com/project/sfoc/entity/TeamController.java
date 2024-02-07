package com.project.sfoc.entity;

import com.project.sfoc.team.TeamRequestDto;
import com.project.sfoc.team.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Slf4j
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/")
    public void createTeam(@RequestBody TeamRequestDto teamRequestDto) {
        teamService.createTeam(teamRequestDto);
        log.info("팀 생성");
    }

}


