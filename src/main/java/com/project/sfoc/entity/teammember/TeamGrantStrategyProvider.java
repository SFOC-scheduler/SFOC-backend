package com.project.sfoc.entity.teammember;

import com.project.sfoc.entity.teammember.strategy.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@RequiredArgsConstructor
public class TeamGrantStrategyProvider {

    private final Map<TeamGrant, TeamGrantUpdateStrategy> update;

    private final Map<TeamGrant, TeamMemberDeleteStrategy> delete;


    public TeamGrantUpdateStrategy getUpdateStrategy(TeamGrant type) {
        return update.get(type);
    }

    public TeamMemberDeleteStrategy getDeleteStrategy(TeamGrant type) {

        return delete.get(type);
    }
}
