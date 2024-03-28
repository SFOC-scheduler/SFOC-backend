package com.project.sfoc.entity.teammember;

import com.project.sfoc.entity.teammember.dto.RequestDeleteTeamMemberDto;
import com.project.sfoc.entity.teammember.dto.ResponseTeamMemberDto;
import com.project.sfoc.entity.teammember.dto.RequestUpdateTeamGrantDto;
import com.project.sfoc.entity.teammember.strategy.TeamGrantUpdateStrategy;
import com.project.sfoc.entity.teammember.strategy.TeamMemberDeleteStrategy;
import com.project.sfoc.exception.EntityNotFoundException;
import com.project.sfoc.exception.Error;
import com.project.sfoc.exception.IllegalDtoException;
import com.project.sfoc.exception.PermissionDeniedError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.sfoc.entity.teammember.TeamGrant.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamGrantStrategyProvider provider;


    public List<ResponseTeamMemberDto> findTeamMembers(Long teamId, Long userId) {

        TeamMember requestMember = findTeamMemberByTeamAndUser(teamId, userId);
        TeamGrant teamGrant = requestMember.getTeamGrant();

        if(isAdmin(teamGrant)) {
            List<TeamMember> teamMembers = teamMemberRepository.findByTeam_Id(teamId);
            return teamMembers.stream().
                    map(teamMember -> ResponseTeamMemberDto.of(teamMember.getId(), teamMember.getUserNickname(), teamMember.getTeamGrant())).toList();
        } else {
            throw new PermissionDeniedError(Error.DENIED_ACCESS);
        }
    }

    public void deleteTeamMember(RequestDeleteTeamMemberDto dto, Long teamId, Long userId) {
        TeamMember admin = findTeamMemberByTeamAndUser(teamId, userId);
        TeamMember deleteMember = findById(dto.teamMemberId());

        if (!deleteMember.getTeam().getId().equals(teamId)) {
            throw new IllegalDtoException(Error.INVALID_DTO);
        }

        TeamMemberDeleteStrategy strategy = provider.getDeleteStrategy(admin.getTeamGrant());
        strategy.delete(admin, deleteMember);

    }

    public void updateTeamGrant(RequestUpdateTeamGrantDto dto, Long teamId, Long userId) {
        TeamMember admin = findTeamMemberByTeamAndUser(teamId, userId);
        TeamMember updateTeamMember = findById(dto.teamMemberId());

        if (!updateTeamMember.getTeam().getId().equals(teamId)) {
            throw new IllegalDtoException(Error.INVALID_DTO);
        }

        TeamGrantUpdateStrategy strategy = provider.getUpdateStrategy(admin.getTeamGrant());
        strategy.update(admin, updateTeamMember, dto);
    }


    private TeamMember findTeamMemberByTeamAndUser(Long teamId, Long userId) {
        return teamMemberRepository.findByTeam_IdAndUser_Id(teamId, userId).orElseThrow(() -> new EntityNotFoundException(Error.INVALID_DTO));
    }

    private TeamMember findById(Long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId).orElseThrow(() -> new EntityNotFoundException(Error.INVALID_DTO));
    }

    private boolean isAdmin(TeamGrant teamGrant) {
        return teamGrant.equals(HIGHEST_ADMIN) || teamGrant.equals(MIDDLE_ADMIN);
    }

}


