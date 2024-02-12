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
    public ResponseEntity<?> createTeam(@RequestBody TeamRequestDto teamRequestDto) {
        teamService.createTeam(teamRequestDto);
        log.info("팀 생성");

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{teamId}/entry")
    public ResponseEntity<Map<String, Object>> setUserNickname(@RequestBody String userNickname,
                                                               @PathVariable Long teamId, Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        TeamGrant teamGrant = teamService.entryTeam(TeamMemberDto.of(userNickname, userId, teamId));

        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("teamId", teamId);
        map.put("userNickname", userNickname);
        map.put("grant", teamGrant);

        return ResponseEntity.ok(map);
    }

}


