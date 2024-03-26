package com.project.sfoc.entity.teammember;

import com.project.sfoc.security.jwt.UserInfo;
import com.project.sfoc.entity.teammember.dto.DeleteTeamMemberDto;
import com.project.sfoc.entity.teammember.dto.TeamMemberResponseDto;
import com.project.sfoc.entity.teammember.dto.UpdateTeamGrantDto;
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
    public ResponseEntity<List<TeamMemberResponseDto>> findAllTeamMembers(@PathVariable(name = "teamId") Long teamId,
                                                                          @AuthenticationPrincipal UserInfo userInfo) {

        Long userId = userInfo.id();

        List<TeamMemberResponseDto> teamMembers = teamMemberService.findTeamMembers(teamId, userId);

        return ResponseEntity.ok(teamMembers);
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<DeleteTeamMemberDto> deleteTeamMember(@RequestBody DeleteTeamMemberDto deleteTeamMemberDto,
                                            @PathVariable(name = "teamId") Long teamId, @AuthenticationPrincipal UserInfo userInfo) {

        Long userId = userInfo.id();

        teamMemberService.deleteTeamMember(deleteTeamMemberDto, teamId, userId);

        return ResponseEntity.ok(deleteTeamMemberDto);
    }

    @PatchMapping("/{teamId}/grant")
    public ResponseEntity<UpdateTeamGrantDto> updateTeamGrant(@RequestBody UpdateTeamGrantDto updateTeamGrantDto,
                                                              @PathVariable(name = "teamId") Long teamId, @AuthenticationPrincipal UserInfo userInfo) {

        Long userId = userInfo.id();

        UpdateTeamGrantDto updateDto = teamMemberService.updateTeamGrant(updateTeamGrantDto, teamId, userId);

        return ResponseEntity.ok(updateDto);
    }
}
