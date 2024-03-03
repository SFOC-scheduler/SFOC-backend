package com.project.sfoc.teammember;

import com.project.sfoc.security.jwt.UserInfo;
import com.project.sfoc.teammember.dto.DeleteTeamMemberDto;
import com.project.sfoc.teammember.dto.TeamMemberResponseDto;
import com.project.sfoc.teammember.dto.UpdateTeamGrantDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;


    @GetMapping("{teamId}/members")
    public ResponseEntity<List<TeamMemberResponseDto>> findAllTeamMembers(@PathVariable Long teamId, Authentication authentication) {

        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        Long userId = userInfo.id();

        List<TeamMemberResponseDto> teamMembers = teamMemberService.findTeamMembers(teamId, userId);

        return ResponseEntity.ok(teamMembers);
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<DeleteTeamMemberDto> deleteTeamMember(@RequestBody DeleteTeamMemberDto deleteTeamMemberDto,
                                                                @PathVariable Long teamId, Authentication authentication) {

        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        Long userId = userInfo.id();

        teamMemberService.deleteTeamMember(deleteTeamMemberDto, teamId, userId);

        return ResponseEntity.ok(deleteTeamMemberDto);
    }

    @PatchMapping("/{teamId}/grant")
    public ResponseEntity<UpdateTeamGrantDto> updateTeamGrant(@RequestBody UpdateTeamGrantDto updateTeamGrantDto,
                                                              @PathVariable Long teamId, Authentication authentication) {

        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        Long userId = userInfo.id();

        UpdateTeamGrantDto updateDto = teamMemberService.updateTeamGrant(updateTeamGrantDto, teamId, userId);

        return ResponseEntity.ok(updateDto);
    }
}
