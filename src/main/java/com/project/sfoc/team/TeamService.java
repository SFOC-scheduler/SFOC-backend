package com.project.sfoc.team;

import com.project.sfoc.team.dto.*;
import com.project.sfoc.teammember.TeamGrant;
import com.project.sfoc.teammember.TeamMember;
import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.user.UserRepository;
import com.project.sfoc.teammember.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.project.sfoc.teammember.TeamGrant.*;


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

    //TODO: 같은 user 중복 방지

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


    // 팀 설정을 위한 팀 정보 조회
    public AbstractTeamInfoDto getTeamInfo(Long teamId, Long userId) {
        Team team = findTeamByTeamId(teamId);
        TeamMember teamMember = findTeamMemberByTeamAndUser(teamId, userId);
        TeamGrant teamGrant = teamMember.getTeamGrant();
        if(teamGrant == NORMAL) {
            return AbstractTeamInfoDto.from(teamMember);

        } else {
            return AbstractTeamInfoDto.from(team, teamMember);
        }

    }

    public void updateTeamInfo(UpdateTeamInfo teamInfoDto, Long teamId, Long userId) {
        TeamMember teamMember = findTeamMemberByTeamAndUser(teamId, userId);

        TeamGrant teamGrant = teamMember.getTeamGrant();

        if (teamGrant == HIGHEST_ADMIN || teamGrant == MIDDLE_ADMIN) {

            Team team = findTeamByTeamId(teamId);
            team.update(teamInfoDto.teamName(), teamInfoDto.description(), teamInfoDto.disclosure());
            teamMember.update(teamInfoDto.teamNickname(), teamInfoDto.userNickname());

        } else {
            teamMember.update(teamInfoDto.teamNickname(), teamInfoDto.userNickname());
        }

    }

    private String createInvitationCode() {
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

    private TeamMember findTeamMemberByTeamAndUser(Long teamId, Long userId) {
        return teamMemberRepository.findByTeam_IdAndUser_Id(teamId, userId).orElseThrow(IllegalArgumentException::new);
    }


}
