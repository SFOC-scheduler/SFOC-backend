package com.project.sfoc.entity.team;

import com.project.sfoc.entity.team.dto.*;
import com.project.sfoc.entity.teammember.TeamGrant;
import com.project.sfoc.security.jwt.UserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Slf4j
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<List<ResponseTeamInfoDto>> getTeams(@AuthenticationPrincipal UserInfo userInfo) {

        Long userId = userInfo.id();

        List<ResponseTeamInfoDto> teamsInfoDto = teamService.getTeams(userId);

        return ResponseEntity.ok(teamsInfoDto);
    }
    @PostMapping
    public ResponseEntity<Void> createTeam(@RequestBody @Valid RequestTeamDto requestTeamDto,
                                        @AuthenticationPrincipal UserInfo userInfo) {

        Long userId = userInfo.id();

        teamService.createTeam(requestTeamDto, userId);
        log.info("팀 생성");
        return ResponseEntity.ok().build();
    }

    //post?
    @PostMapping("/{teamId}/entry")
    public ResponseEntity<Void> entryTeam(@PathVariable(name = "teamId") Long teamId, @AuthenticationPrincipal UserInfo userInfo) {
        teamService.entryTeam(TeamMemberDto.of(userInfo.id(), teamId, TeamGrant.NORMAL));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<AbstractTeamInfoDto> getTeamInfo(@PathVariable(name = "teamId") Long teamId, @AuthenticationPrincipal UserInfo userInfo) {
        AbstractTeamInfoDto teamInfo = teamService.getTeamInfo(teamId, userInfo.id());
        return ResponseEntity.ok(teamInfo);
    }

    @PatchMapping("/{teamId}")
    public ResponseEntity<Void> setTeam(@RequestBody RequestUpdateTeamInfo teamInfoDto,
                                                  @PathVariable(name = "teamId") Long teamId, @AuthenticationPrincipal UserInfo userInfo) {
        teamService.updateTeamInfo(teamInfoDto, teamId, userInfo.id());
        log.info("팀 설정 수정");

        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ResponseTeamSearchInfoDto>> searchTeam(@RequestBody RequestTeamSearchDto request,
                                                                      @PageableDefault(size=10) Pageable pageable) {
        Page<ResponseTeamSearchInfoDto> teamSearchInfoDtos = teamService.searchTeam(request, pageable);
        return ResponseEntity.ok(teamSearchInfoDtos);
    }

}


