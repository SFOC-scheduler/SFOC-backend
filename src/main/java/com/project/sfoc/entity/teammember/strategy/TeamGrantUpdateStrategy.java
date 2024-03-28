package com.project.sfoc.entity.teammember.strategy;

import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.teammember.dto.RequestUpdateTeamGrantDto;

@FunctionalInterface
public interface TeamGrantUpdateStrategy {

    void update(TeamMember admin, TeamMember updateTeamMember, RequestUpdateTeamGrantDto dto);
}
