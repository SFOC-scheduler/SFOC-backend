package com.project.sfoc.entity.teammember.strategy;

import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.teammember.dto.RequestUpdateTeamGrantDto;
import com.project.sfoc.exception.Error;
import com.project.sfoc.exception.PermissionDeniedError;

public class TeamGrantUpdateNormalStrategy implements TeamGrantUpdateStrategy {
    @Override
    public void update(TeamMember admin, TeamMember updateTeamMember, RequestUpdateTeamGrantDto dto) {
        throw new PermissionDeniedError(Error.DENIED_ACCESS);
    }
}
