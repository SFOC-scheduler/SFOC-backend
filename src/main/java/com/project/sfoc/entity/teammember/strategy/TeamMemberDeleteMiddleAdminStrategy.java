package com.project.sfoc.entity.teammember.strategy;

import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.teammember.TeamMemberRepository;
import com.project.sfoc.exception.Error;
import com.project.sfoc.exception.IllegalDtoException;
import lombok.RequiredArgsConstructor;


import static com.project.sfoc.entity.teammember.TeamGrant.*;

@RequiredArgsConstructor
public class TeamMemberDeleteMiddleAdminStrategy implements TeamMemberDeleteStrategy{

    private final TeamMemberRepository teamMemberRepository;
    @Override
    public void delete(TeamMember admin, TeamMember deleteMember) {

        if (deleteMember.getTeamGrant() == NORMAL) {
            teamMemberRepository.delete(deleteMember);
        } else {
            throw new IllegalDtoException("중간 관리자는 관리자를 탈퇴할 수 없습니다", Error.INVALID_DTO);
        }
    }
}
