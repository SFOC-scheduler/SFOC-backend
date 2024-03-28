package com.project.sfoc.entity.teammember.strategy;

import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.teammember.dto.RequestUpdateTeamGrantDto;
import com.project.sfoc.exception.Error;
import com.project.sfoc.exception.IllegalDtoException;

import static com.project.sfoc.entity.teammember.TeamGrant.*;

public class TeamGrantUpdateMiddleAdminStrategy implements TeamGrantUpdateStrategy {

    @Override
    public void update(TeamMember admin, TeamMember updateTeamMember, RequestUpdateTeamGrantDto dto) {

        if(admin.equals(updateTeamMember) && dto.teamGrant().equals(NORMAL)) {
            updateTeamMember.updateTeamGrant(NORMAL);
        } else {
            throw new IllegalDtoException("중간 관리자는 자신의 권한을 일반 참가자로 바꾸는 것 말고는 권한을 바꿀 수 없습니다.",
                    Error.INVALID_DTO);
        }
    }
}
