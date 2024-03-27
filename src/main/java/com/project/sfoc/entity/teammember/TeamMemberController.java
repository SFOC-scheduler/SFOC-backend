package com.project.sfoc.entity.teammember;

import com.project.sfoc.security.jwt.UserInfo;
import com.project.sfoc.entity.teammember.dto.RequestDeleteTeamMemberDto;
import com.project.sfoc.entity.teammember.dto.ResponseTeamMemberDto;
import com.project.sfoc.entity.teammember.dto.RequestUpdateTeamGrantDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;


    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<ResponseTeamMemberDto>> findAllTeamMembers(@PathVariable(name = "teamId") Long teamId,
                                                                          @AuthenticationPrincipal UserInfo userInfo) {

        Long userId = userInfo.id();

        List<ResponseTeamMemberDto> teamMembers = teamMemberService.findTeamMembers(teamId, userId);

        return ResponseEntity.ok(teamMembers);
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<RequestDeleteTeamMemberDto> deleteTeamMember(@RequestBody RequestDeleteTeamMemberDto requestDeleteTeamMemberDto,
                                                                       @PathVariable(name = "teamId") Long teamId, @AuthenticationPrincipal UserInfo userInfo) {

        Long userId = userInfo.id();

        teamMemberService.deleteTeamMember(requestDeleteTeamMemberDto, teamId, userId);

        return ResponseEntity.ok(requestDeleteTeamMemberDto);
    }

    @PatchMapping("/{teamId}/grant")
    public ResponseEntity<Void> updateTeamGrant(@RequestBody RequestUpdateTeamGrantDto requestUpdateTeamGrantDto,
                                                                     @PathVariable(name = "teamId") Long teamId, @AuthenticationPrincipal UserInfo userInfo) {

        Long userId = userInfo.id();

        teamMemberService.updateTeamGrant(requestUpdateTeamGrantDto, teamId, userId);

        return ResponseEntity.ok().build();
    }
}
