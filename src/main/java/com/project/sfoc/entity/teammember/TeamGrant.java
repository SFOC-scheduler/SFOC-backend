package com.project.sfoc.entity.teammember;

import com.project.sfoc.entity.team.Team;
import com.project.sfoc.entity.team.dto.AbstractTeamInfoDto;
import com.project.sfoc.entity.team.dto.RequestUpdateTeamInfo;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.BiFunction;

@RequiredArgsConstructor
public enum TeamGrant {

    HIGHEST_ADMIN((teamInform ,team, teamMember) -> {team.update(teamInform.teamName(),
            teamInform.description(), teamInform.disclosure());
            teamMember.update(teamInform.teamNickname(), teamInform.userNickname());
    }, (AbstractTeamInfoDto::from))

    ,MIDDLE_ADMIN((teamInform ,team, teamMember) -> {team.update(teamInform.teamName(),
            teamInform.description(), teamInform.disclosure());
        teamMember.update(teamInform.teamNickname(), teamInform.userNickname());
    }, (AbstractTeamInfoDto::from))

    ,NORMAL((teamInform ,team, teamMember) -> {
        team.update(teamInform.teamName(),
                teamInform.description(), teamInform.disclosure());
    }, ((team, teamMember) -> AbstractTeamInfoDto.from(teamMember)));

    private final TriConsumer<RequestUpdateTeamInfo, Team, TeamMember> update;

    private final BiFunction<Team, TeamMember, AbstractTeamInfoDto> getInfo;


    public void update(RequestUpdateTeamInfo updateTeamInfo, Team team, TeamMember teamMember) {
        this.update.accept(updateTeamInfo, team, teamMember);
    }

    public AbstractTeamInfoDto getInfo(Team team, TeamMember teamMember) {
        return this.getInfo.apply(team, teamMember);
    }
}
