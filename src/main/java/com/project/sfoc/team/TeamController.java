package com.project.sfoc.team;

import com.project.sfoc.team.dto.*;
import com.project.sfoc.teammember.TeamGrant;
import com.project.sfoc.security.jwt.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Slf4j
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody TeamRequestDto teamRequestDto, Authentication authentication) {

        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        Long userId = userInfo.id();

        teamService.createTeam(teamRequestDto, userId);
        log.info("팀 생성");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{teamId}/entry")
    public ResponseEntity<TeamMemberDto> setUserNickname(@RequestBody UserNicknameDto userNicknameDto,
                                                         @PathVariable Long teamId, @AuthenticationPrincipal UserInfo userInfo) {
        TeamMemberDto teamMemberDto = teamService.entryTeam(TeamMemberDto.of(userInfo.id(), teamId, userNicknameDto.userNickname(), TeamGrant.NORMAL));
        return ResponseEntity.ok(teamMemberDto);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<AbstractTeamInfoDto> getTeamInfo(@PathVariable Long teamId, @AuthenticationPrincipal UserInfo userInfo) {
        AbstractTeamInfoDto teamInfo = teamService.getTeamInfo(teamId, userInfo.id());
        return ResponseEntity.ok(teamInfo);
    }

    @PatchMapping("/{teamId}")
    public ResponseEntity<UpdateTeamInfo> setTeam(@RequestBody UpdateTeamInfo teamInfoDto,
                                     @PathVariable Long teamId, @AuthenticationPrincipal UserInfo userInfo) {
        teamService.updateTeamInfo(teamInfoDto, teamId, userInfo.id());
        log.info("팀 설정 수정");

        return ResponseEntity.ok(teamInfoDto);
    }

}


