package com.project.sfoc.team;

import com.project.sfoc.entity.TeamGrant;
import com.project.sfoc.security.CustomOAuth2User;
import com.project.sfoc.security.jwt.UserClaims;
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

        UserClaims userClaims = (UserClaims) authentication.getPrincipal();
        Long userId = userClaims.id();

        teamService.createTeam(teamRequestDto, userId);
        log.info("팀 생성");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{teamId}/entry")
    public ResponseEntity<TeamMemberDto> setUserNickname(@RequestBody UserNicknameDto userNicknameDto,
                                                               @PathVariable Long teamId, Authentication authentication) {

        UserClaims userClaims = (UserClaims) authentication.getPrincipal();
        Long userId = userClaims.id();

        TeamMemberDto teamMemberDto = teamService.entryTeam(TeamMemberDto.of(userId, teamId, userNicknameDto.userNickname(), TeamGrant.NORMAL));


        return ResponseEntity.ok(teamMemberDto);
    }

}


