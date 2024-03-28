package com.project.sfoc.entity.teammember.strategy;

import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.teammember.dto.RequestUpdateTeamGrantDto;
import com.project.sfoc.exception.Error;
import com.project.sfoc.exception.IllegalDtoException;

import static com.project.sfoc.entity.teammember.TeamGrant.*;


public class TeamGrantUpdateHighestAdminStrategy implements TeamGrantUpdateStrategy {


    @Override
    public void update(TeamMember admin, TeamMember updateTeamMember, RequestUpdateTeamGrantDto dto) {

        if (admin.equals(updateTeamMember) && !dto.teamGrant().equals(HIGHEST_ADMIN)) {
            throw new IllegalDtoException("최상위 관리자를 위임해야 변경 가능합니다.", Error.INVALID_DTO);
        }

        if (dto.teamGrant().equals(HIGHEST_ADMIN)) {
            admin.updateTeamGrant(MIDDLE_ADMIN);
        }

        updateTeamMember.updateTeamGrant(dto.teamGrant());
    }
}
