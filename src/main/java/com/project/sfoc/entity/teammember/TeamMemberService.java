package com.project.sfoc.entity.teammember;

import com.project.sfoc.entity.team.TeamRepository;
import com.project.sfoc.entity.teammember.dto.RequestDeleteTeamMemberDto;
import com.project.sfoc.entity.teammember.dto.ResponseTeamMemberDto;
import com.project.sfoc.entity.teammember.dto.RequestUpdateTeamGrantDto;
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
import static com.project.sfoc.entity.teammember.TeamGrant.HIGHEST_ADMIN;
import static com.project.sfoc.entity.teammember.TeamGrant.NORMAL;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;


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
        TeamGrant teamGrant = admin.getTeamGrant();

        if(teamGrant.equals(HIGHEST_ADMIN)) {
            if(admin.getId().equals(deleteMember.getId())) {
                if (teamMemberRepository.countByTeam_Id(teamId) != 1) {
                    log.info("최상위 관리자는 혼자이거나 권한을 위임해야 팀에서 나갈 수 있습니다.");
                    throw new IllegalDtoException("최상위 관리자는 혼자이거나 권한을 위임해야 팀에서 나갈 수 있습니다.",
                            Error.INVALID_DTO);
                } else {
                    teamMemberRepository.delete(admin);
                    teamRepository.deleteById(teamId);
                }
            } else {
                teamMemberRepository.delete(deleteMember);
            }
        }
        else if(teamGrant.equals(MIDDLE_ADMIN)) {
            if(deleteMember.getTeamGrant() == NORMAL) {
                teamMemberRepository.delete(deleteMember);
            } else {
                log.info("중간 관리자는 관리자를 탈퇴할 수 없습니다");
                throw new IllegalDtoException(Error.INVALID_DTO);
            }
        }
        else {
            throw new PermissionDeniedError(Error.DENIED_ACCESS);
        }
    }

    // 다른 유저를 HIGHEST_ADMIN으로 바꾸면 상대방의 TeamGrant로 바꿈
    public void updateTeamGrant(RequestUpdateTeamGrantDto dto, Long teamId, Long userId) {
        TeamMember admin = findTeamMemberByTeamAndUser(teamId, userId);
        TeamMember updateTeamMember = findById(dto.teamMemberId());

        if (!updateTeamMember.getTeam().getId().equals(teamId)) {
            throw new IllegalDtoException(Error.INVALID_DTO);
        }

        if(admin.getTeamGrant().equals(HIGHEST_ADMIN)) {

            if (dto.teamGrant().equals(HIGHEST_ADMIN)) {
                admin.updateTeamGrant(updateTeamMember.getTeamGrant());
            }
            updateTeamMember.updateTeamGrant(dto.teamGrant());
        } else {
            throw new PermissionDeniedError(Error.DENIED_ACCESS);
        }
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


