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
        Team team = Team.of("팀 이름", "초대 코드", "팀 설명", Disclosure.PUBLIC);
        TeamMember teamMember = TeamMember.of("팀 닉네임", "사용자 닉네임", TeamGrant.HIGHEST_ADMIN, user, team);

        LocalDateTime now = LocalDateTime.now();
        Schedule schedule = Schedule.of("제목", "설명", teamMember,
                PeriodRepeat.of(PeriodType.DAY, 2L, List.of(now.toLocalDate()),
                        RepeatType.COUNT, 3, null));
        List<SubSchedule> subSchedules = IntStream.range(0, 3)
                .mapToObj(count -> SubSchedule.of(Boolean.FALSE, Boolean.FALSE,
                        now.plusDays(count * 2L), now.plusDays(count * 2L).plusHours(1L), schedule))
                .toList();

        userRepository.save(user);
        teamRepository.save(team);
        teamMemberRepository.save(teamMember);
        scheduleRepository.save(schedule);
        subScheduleRepository.saveAll(subSchedules);
    }

}
