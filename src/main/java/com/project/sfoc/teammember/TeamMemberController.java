package com.project.sfoc.teammember;

import com.project.sfoc.security.jwt.UserClaims;
import com.project.sfoc.teammember.dto.TeamGrantDto;
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
    public ResponseEntity<List<TeamGrantDto>> findAllTeamMembers(@PathVariable Long teamId) {
        List<TeamGrantDto> teamMembers = teamMemberService.findTeamMembers(teamId);

        return ResponseEntity.ok(teamMembers);
    }

    @DeleteMapping("{teamId}/members")
    public ResponseEntity<TeamGrantDto> deleteTeamMember(@RequestBody TeamGrantDto teamGrantDto,
                                                 @PathVariable Long teamId, Authentication authentication) {

        UserClaims userClaim = (UserClaims) authentication.getPrincipal();
        Long userId = userClaim.id();

        teamMemberService.deleteTeamMember(teamGrantDto, teamId, userId);

        return ResponseEntity.ok(teamGrantDto);
    }

    @PatchMapping("{teamId}/grant")
    public ResponseEntity<TeamGrantDto> updateTeamGrant(@RequestBody TeamGrantDto teamGrantDto,
                                             @PathVariable Long teamId, Authentication authentication) {

        UserClaims userClaim = (UserClaims) authentication.getPrincipal();
        Long userId = userClaim.id();

        TeamGrantDto updateDto = teamMemberService.updateTeamGrant(teamGrantDto, teamId, userId);

        return ResponseEntity.ok(updateDto);
    }
}
