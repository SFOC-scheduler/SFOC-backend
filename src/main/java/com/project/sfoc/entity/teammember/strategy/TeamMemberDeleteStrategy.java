package com.project.sfoc.entity.teammember.strategy;

import com.project.sfoc.entity.teammember.TeamMember;

@FunctionalInterface
public interface TeamMemberDeleteStrategy {

    void delete(TeamMember admin, TeamMember deleteMember);
}
