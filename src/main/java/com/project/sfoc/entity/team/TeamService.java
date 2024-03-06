package com.project.sfoc.entity.team;

import com.project.sfoc.entity.team.dto.AbstractTeamInfoDto;
import com.project.sfoc.entity.team.dto.TeamMemberDto;
import com.project.sfoc.entity.team.dto.TeamRequestDto;
import com.project.sfoc.entity.team.dto.UpdateTeamInfo;
import com.project.sfoc.entity.teammember.TeamGrant;
import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.user.UserRepository;
import com.project.sfoc.entity.teammember.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.project.sfoc.entity.teammember.TeamGrant.*;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;


    //TODO 팀 중복 어떻게?
    public void createTeam(TeamRequestDto teamRequestDto, Long userId) {
        Team team = teamRequestDto.toEntity(createInvitationCode());
        Team savedteam = teamRepository.save(team);

        entryTeam(TeamMemberDto.of(userId, savedteam.getId(), teamRequestDto.userNickname(), HIGHEST_ADMIN));
    }


    public TeamMemberDto entryTeam(TeamMemberDto teamMemberDto) {


        if (isPresentTeamMember(teamMemberDto.teamId(),teamMemberDto.userId())) {
            log.info("이미 팀에 존재합니다.");
            throw new IllegalArgumentException();
        }

        User user = findUserByUserId(teamMemberDto.userId());
        Team team = findTeamByTeamId(teamMemberDto.teamId());

        if (isDuplicateTeamUserNickname(teamMemberDto.userNickname(), teamMemberDto.teamId())) {
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

    // TODO 팀 권한에 따라 분리 시키기
    public void updateTeamInfo(UpdateTeamInfo teamInfoDto, Long teamId, Long userId) {
        TeamMember teamMember = findTeamMemberByTeamAndUser(teamId, userId);

        TeamGrant teamGrant = teamMember.getTeamGrant();

        if (teamGrant == HIGHEST_ADMIN || teamGrant == MIDDLE_ADMIN) {

            Team team = findTeamByTeamId(teamId);
            team.update(teamInfoDto.teamName(), teamInfoDto.description(), teamInfoDto.disclosure());
            teamMember.update(teamInfoDto.teamNickname(), teamInfoDto.userNickname());

        } else {

            if (isDuplicateTeamUserNickname(teamInfoDto.userNickname(), teamId)) {
                log.info("닉네임 중복 오류");
                throw new IllegalArgumentException();
            }

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

    private boolean isPresentTeamMember(Long teamId, Long userId) {
        return teamMemberRepository.existsByTeam_IdAndUser_Id(teamId, userId);
    }

    private boolean isDuplicateTeamUserNickname(String userNickname, Long teamId) {
        return teamMemberRepository.existsByUserNicknameAndTeam_Id(userNickname, teamId);
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
