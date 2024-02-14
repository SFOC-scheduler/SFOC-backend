package com.project.sfoc.team;

import com.project.sfoc.entity.TeamGrant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Slf4j
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody TeamRequestDto teamRequestDto, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        teamService.createTeam(teamRequestDto, userId);
        log.info("팀 생성");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{teamId}/entry")
    public ResponseEntity<TeamMemberDto> setUserNickname(@RequestBody Map<String, String> userNickname,
                                                               @PathVariable Long teamId, Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        TeamMemberDto teamMemberDto = teamService.entryTeam(TeamMemberDto.of(userId, teamId, userNickname.get("userNickname"), TeamGrant.NORMAL));


        return ResponseEntity.ok(teamMemberDto);
    }

}


