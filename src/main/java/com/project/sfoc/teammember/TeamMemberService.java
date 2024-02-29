package com.project.sfoc.teammember;

import com.project.sfoc.teammember.dto.DeleteTeamMemberDto;
import com.project.sfoc.teammember.dto.TeamMemberResponseDto;
import com.project.sfoc.teammember.dto.UpdateTeamGrantDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;


    public List<TeamMemberResponseDto> findTeamMembers(Long teamId, Long userId) {

        TeamMember requestMember = findTeamMemberByTeamAndUser(teamId, userId);
        TeamGrant teamGrant = requestMember.getTeamGrant();

        if(teamGrant == TeamGrant.HIGHEST_ADMIN || teamGrant == TeamGrant.MIDDLE_ADMIN) {
            List<TeamMember> teamMembers = teamMemberRepository.findByTeam_Id(teamId);

            return teamMembers.stream().
                    map(teamMember -> TeamMemberResponseDto.of(teamMember.getId(), teamMember.getUserNickname(), teamMember.getTeamGrant())).toList();
        } else {
            log.info("권한이 없습니다.");
            throw new IllegalStateException();
        }
    }

    public void deleteTeamMember(DeleteTeamMemberDto deleteTeamMemberDto, Long teamId, Long userId) {
        TeamMember admin = findTeamMemberByTeamAndUser(teamId, userId);
        TeamGrant teamGrant = admin.getTeamGrant();

        if(teamGrant == TeamGrant.MIDDLE_ADMIN || teamGrant == TeamGrant.HIGHEST_ADMIN) {

            TeamMember deleteMember = findById(deleteTeamMemberDto.teamMemberId());
            if(deleteMember.getTeamGrant() == TeamGrant.NORMAL) {
                teamMemberRepository.deleteById(deleteTeamMemberDto.teamMemberId());
            } else {
                log.info("관리자는 팀에서 나갈 수 없습니다.");
                throw new IllegalStateException();
            }
        } else {
            log.info("권한이 없습니다.");
            throw new IllegalStateException();
        }
    }

    //TODO: 최상위 계층으로 변경하면 자신의 권한이 바뀌어야 함.

    public UpdateTeamGrantDto updateTeamGrant(UpdateTeamGrantDto updateTeamGrantDto, Long teamId, Long userId) {
        TeamMember admin = findTeamMemberByTeamAndUser(teamId, userId);

        if(admin.getTeamGrant() == TeamGrant.HIGHEST_ADMIN) {

            if (updateTeamGrantDto.teamGrant() == TeamGrant.HIGHEST_ADMIN) {
                log.info("최상위 관리자는 한 명 이상 될 수 없습니다.");
                throw new IllegalStateException();
            }

            TeamMember updateTeamMember = findById(updateTeamGrantDto.teamMemberId());
            updateTeamMember.updateTeamGrant(updateTeamGrantDto.teamGrant());
            return UpdateTeamGrantDto.of(updateTeamGrantDto.teamMemberId(), updateTeamGrantDto.teamGrant());
        } else {
            log.info("권한이 없습니다.");
            throw new IllegalStateException();
        }
    }


    private TeamMember findTeamMemberByTeamAndUser(Long teamId, Long userId) {
        return teamMemberRepository.findByTeam_IdAndUser_Id(teamId, userId).orElseThrow(IllegalArgumentException::new);
    }

    private TeamMember findById(Long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId).orElseThrow(IllegalArgumentException::new);
    }

}


