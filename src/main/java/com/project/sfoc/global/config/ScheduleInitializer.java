package com.project.sfoc.global.config;

import com.project.sfoc.entity.schedule.*;
import com.project.sfoc.entity.team.Disclosure;
import com.project.sfoc.entity.team.Team;
import com.project.sfoc.entity.team.TeamRepository;
import com.project.sfoc.entity.teammember.TeamGrant;
import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.teammember.TeamMemberRepository;
import com.project.sfoc.entity.user.Provider;
import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class ScheduleInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ScheduleRepository scheduleRepository;
    private final SubScheduleRepository subScheduleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user = User.of(Provider.GOOGLE, "songwoo1015@gmail.com", "111116899498022725865");
        User user1 = User.of(Provider.GOOGLE, "더미 유저1", "더미 유저 식별자1");
        User user2 = User.of(Provider.GOOGLE, "더미 유저2", "더미 유저 식별자2");

        Team team = Team.of("팀 이름", "초대 코드", "팀 설명", Disclosure.PUBLIC);
        Team team1 = Team.of("더미 팀", "초대 코드1", "팀 설명", Disclosure.PUBLIC);

        TeamMember teamMember = TeamMember.of("팀 닉네임", "사용자 닉네임", TeamGrant.HIGHEST_ADMIN, user, team);
        TeamMember teamMember1 = TeamMember.of("팀 닉네임", "더미1", TeamGrant.NORMAL, user1, team);
        TeamMember teamMember2 = TeamMember.of("팀 닉네임", "더미2", TeamGrant.HIGHEST_ADMIN, user2, team1);

        LocalDateTime now = LocalDateTime.now();

        Schedule schedule1 = Schedule.of("제목", "설명", teamMember,
                PeriodRepeat.of(PeriodType.DAY, 2L, List.of(now.toLocalDate()),
                        RepeatType.COUNT, 3, null));
        Schedule schedule2 = Schedule.of("제목", "설명", teamMember1,
                PeriodRepeat.of(PeriodType.DAY, 3L, List.of(now.toLocalDate()),
                        RepeatType.COUNT, 2, null));

        List<SubSchedule> subSchedules = new ArrayList<>();
        subSchedules.addAll(IntStream.range(0, 3)
                .mapToObj(count -> SubSchedule.of(Boolean.FALSE, Boolean.FALSE,
                        now.plusDays(count * 2L), now.plusDays(count * 2L).plusHours(1L), schedule1))
                .toList());
        subSchedules.addAll(IntStream.range(0, 2)
                .mapToObj(count -> SubSchedule.of(Boolean.FALSE, Boolean.FALSE,
                        now.plusDays(count * 3L), now.plusDays(count * 3L).plusHours(1L), schedule2))
                .toList());

        userRepository.saveAll(List.of(user, user1, user2));
        teamRepository.saveAll(List.of(team, team1));
        teamMemberRepository.saveAll(List.of(teamMember, teamMember1, teamMember2));
        scheduleRepository.saveAll(List.of(schedule1, schedule2));
        subScheduleRepository.saveAll(subSchedules);
    }

}
