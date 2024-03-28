package com.project.sfoc.entity.teammember;

import com.project.sfoc.entity.team.TeamRepository;
import com.project.sfoc.entity.teammember.strategy.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

import static com.project.sfoc.entity.teammember.TeamGrant.*;

@Configuration
@RequiredArgsConstructor
public class StrategyConfig {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;

    @Bean
    public TeamGrantUpdateHighestAdminStrategy teamGrantUpdateHighestAdminStrategy() {
        return new TeamGrantUpdateHighestAdminStrategy();
    }

    @Bean
    public TeamGrantUpdateMiddleAdminStrategy teamGrantUpdateMiddleAdminStrategy() {
        return new TeamGrantUpdateMiddleAdminStrategy();
    }

    @Bean
    public TeamGrantUpdateNormalStrategy teamGrantUpdateNormalStrategy() {
        return new TeamGrantUpdateNormalStrategy();
    }

    @Bean
    public TeamMemberDeleteHighestAdminStrategy teamMemberDeleteHighestAdminStrategy() {
        return new TeamMemberDeleteHighestAdminStrategy(teamMemberRepository, teamRepository);
    }

    @Bean
    public TeamMemberDeleteMiddleAdminStrategy teamMemberDeleteMiddleAdminStrategy() {
        return new TeamMemberDeleteMiddleAdminStrategy(teamMemberRepository);
    }

    @Bean
    public TeamMemberDeleteNormalStrategy teamMemberDeleteNormalStrategy() {
        return new TeamMemberDeleteNormalStrategy(teamMemberRepository);
    }


    @Bean(name = "teamMemberDelete")
    public Map<TeamGrant, TeamMemberDeleteStrategy> delete() {
        Map<TeamGrant, TeamMemberDeleteStrategy> delete = new EnumMap<>(TeamGrant.class);
        delete.put(HIGHEST_ADMIN, teamMemberDeleteHighestAdminStrategy());
        delete.put(MIDDLE_ADMIN, teamMemberDeleteMiddleAdminStrategy());
        delete.put(NORMAL, teamMemberDeleteNormalStrategy());
        return delete;
    }

    @Bean(name = "teamGrantUpdate")
    public Map<TeamGrant, TeamGrantUpdateStrategy> update() {
        Map<TeamGrant, TeamGrantUpdateStrategy> update = new EnumMap<>(TeamGrant.class);
        update.put(HIGHEST_ADMIN, teamGrantUpdateHighestAdminStrategy());
        update.put(MIDDLE_ADMIN, teamGrantUpdateMiddleAdminStrategy());
        update.put(NORMAL, teamGrantUpdateNormalStrategy());
        return update;
    }
}
