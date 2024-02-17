package com.project.sfoc.teammember;

import com.project.sfoc.teammember.dto.TeamGrantDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;


    public List<TeamGrantDto> findTeamMembers(Long teamId) {
        List<TeamMember> teamMembers = teamMemberRepository.findByTeam_Id(teamId);
        return teamMembers.stream().map(teamMember -> new TeamGrantDto(teamMember.getId(), teamMember.getTeamGrant())).toList();
    }

    public void deleteTeamMember(TeamGrantDto teamGrantDto, Long teamId, Long userId) {
        TeamMember admin = findTeamMemberByTeamAndUser(teamId, userId);

        TeamGrant teamGrant = admin.getTeamGrant();
        if(teamGrant == TeamGrant.MIDDLE_ADMIN || teamGrant == TeamGrant.HIGHEST_ADMIN) {

            if(teamGrantDto.teamGrant() == TeamGrant.NORMAL) {
                teamMemberRepository.deleteById(teamGrantDto.teamMemberId());
            }
        }
    }

    public TeamGrantDto updateTeamGrant(TeamGrantDto teamGrantDto, Long teamId, Long userId) {
        TeamMember admin = findTeamMemberByTeamAndUser(teamId, userId);

        if(admin.getTeamGrant() == TeamGrant.HIGHEST_ADMIN) {
            TeamMember updateTeamMember = findById(teamGrantDto.teamMemberId());
            updateTeamMember.updateTeamGrant(teamGrantDto.teamGrant());

            return TeamGrantDto.of(teamGrantDto.teamMemberId(), teamGrantDto.teamGrant());
        }

        return null;
    }


    private TeamMember findTeamMemberByTeamAndUser(Long teamId, Long userId) {
        return teamMemberRepository.findByTeam_IdAndUser_Id(teamId, userId).orElseThrow(IllegalArgumentException::new);
    }

    private TeamMember findById(Long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId).orElseThrow(IllegalArgumentException::new);
    }

}


