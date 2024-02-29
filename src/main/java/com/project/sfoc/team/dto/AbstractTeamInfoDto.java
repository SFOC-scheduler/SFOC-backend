package com.project.sfoc.team.dto;

import com.project.sfoc.team.Disclosure;
import com.project.sfoc.team.Team;
import com.project.sfoc.teammember.TeamMember;
import lombok.Getter;

@Getter
public abstract class AbstractTeamInfoDto {

    protected String teamName;

    protected String description;

    protected Disclosure disclosure;

    protected String teamNickname;
    protected String userNickname;

    public AbstractTeamInfoDto(String teamName, String description, Disclosure disclosure, String teamNickname, String userNickname) {
        this.teamName = teamName;
        this.description = description;
        this.disclosure = disclosure;
        this.teamNickname = teamNickname;
        this.userNickname = userNickname;
    }

    public AbstractTeamInfoDto(String teamNickname, String userNickname) {
        this.teamNickname = teamNickname;
        this.userNickname = userNickname;
    }

    public static NormalTeamInfoDto from(TeamMember teamMember) {

        return new NormalTeamInfoDto(teamMember.getTeamNickname(), teamMember.getUserNickname());
    }

    public static AdminTeamInfoDto from(Team team, TeamMember teamMember) {

        return new AdminTeamInfoDto(team.getName(), team.getDescription(), team.getDisclosure(),
                teamMember.getTeamNickname(), teamMember.getUserNickname());
    }
}
