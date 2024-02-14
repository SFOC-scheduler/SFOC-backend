package com.project.sfoc.team;

import com.project.sfoc.entity.TeamGrant;
import com.project.sfoc.entity.TeamMember;
import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.project.sfoc.entity.TeamGrant.HIGHEST_ADMIN;
import static com.project.sfoc.entity.TeamGrant.NORMAL;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;


    public void createTeam(TeamRequestDto teamRequestDto, Long userId) {
        final Team team = teamRequestDto.toEntity(createInvitationCode());
        teamRepository.save(team);

        entryTeam(TeamMemberDto.of(userId, team.getId(), teamRequestDto.userNickname(), HIGHEST_ADMIN));

    }

    public TeamMemberDto entryTeam(TeamMemberDto teamMemberDto) {

        User user = findUserByUserId(teamMemberDto.userId());
        Team team = findTeamByTeamId(teamMemberDto.teamId());

        if (isDuplicateTeamUserNickname(teamMemberDto.userNickname(), team)) {
            log.info("닉네임 중복 오류");
            throw new IllegalArgumentException();
        }


        TeamMember teamMember = teamMemberDto.toEntity(team.getName(), user, team);
        teamMemberRepository.save(teamMember);

        return TeamMemberDto.from(teamMember);
    }

    public String createInvitationCode() {
        String code = null;
        while(code == null || isDuplicateUuidCode(code)) {
            String randomUUID = UUID.randomUUID().toString();
            String[] split = randomUUID.split("-");
            code = split[0];
        }

        return code;
    }

    private boolean isDuplicateUuidCode(String code) {
        return teamRepository.existsByInvitationCode(code);
    }

    private boolean isDuplicateTeamUserNickname(String userNickname, Team team) {
        return teamMemberRepository.existsByUserNicknameAndTeam(userNickname, team);
    }

    private Team findTeamByTeamId(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(IllegalArgumentException::new);
    }

    private User findUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
    }
}
