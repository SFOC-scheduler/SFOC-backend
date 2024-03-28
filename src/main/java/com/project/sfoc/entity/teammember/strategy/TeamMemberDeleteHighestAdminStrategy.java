package com.project.sfoc.entity.teammember.strategy;

import com.project.sfoc.entity.team.TeamRepository;
import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.teammember.TeamMemberRepository;
import com.project.sfoc.exception.Error;
import com.project.sfoc.exception.IllegalDtoException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TeamMemberDeleteHighestAdminStrategy implements TeamMemberDeleteStrategy{

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    @Override
    public void delete(TeamMember admin, TeamMember deleteMember) {

        Long teamId = admin.getTeam().getId();

        if (admin.equals(deleteMember)) {
            if (teamMemberRepository.countByTeam_Id(teamId) != 1) {
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
}
