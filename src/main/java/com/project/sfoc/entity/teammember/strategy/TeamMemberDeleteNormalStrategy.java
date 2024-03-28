package com.project.sfoc.entity.teammember.strategy;

import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.exception.Error;
import com.project.sfoc.exception.PermissionDeniedError;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TeamMemberDeleteNormalStrategy implements TeamMemberDeleteStrategy{

    @Override
    public void delete(TeamMember admin, TeamMember deleteMember) {
        throw new PermissionDeniedError(Error.DENIED_ACCESS);
    }
}
