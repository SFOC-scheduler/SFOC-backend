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


    public void defaultCreateTeam(TeamRequestDto teamRequestDto) {
        final Team team = teamRequestDto.toEntity(null);
        teamRepository.save(team);
    }


    public void createTeam(TeamRequestDto teamRequestDto) {
        final Team team = teamRequestDto.toEntity(createInvitationCode());
        teamRepository.save(team);
    }

    public TeamGrant entryTeam(TeamMemberDto teamMemberDto) {

        if (isDuplicateTeamUserNickname(teamMemberDto.userNickname())) {
            log.info("닉네임 중복 오류");
            throw new IllegalArgumentException();
        }

        User user = findUserByUserId(teamMemberDto.userId());
        Team team = findTeamByTeamId(teamMemberDto.teamId());

        TeamGrant teamGrant = setAuthority(team);

        TeamMember teamMember = teamMemberDto.toEntity(team.getName(), teamGrant, user, team);
        teamMemberRepository.save(teamMember);
        return teamGrant;
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

    private TeamGrant setAuthority(Team team) {
        if (teamMemberRepository.existsByTeam(team)) {
            return NORMAL;
        }
        else {
            return HIGHEST_ADMIN;
        }
    }

    private boolean isDuplicateUuidCode(String code) {
        return teamRepository.existsByInvitationCode(code);
    }

    private boolean isDuplicateTeamUserNickname(String userNickname) {
        return teamMemberRepository.existsByUserNickname(userNickname);
    }

    private Team findTeamByTeamId(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(IllegalArgumentException::new);
    }

    private User findUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
    }
}
