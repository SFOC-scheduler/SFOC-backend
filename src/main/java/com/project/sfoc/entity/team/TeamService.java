package com.project.sfoc.entity.team;

import com.project.sfoc.entity.team.dto.*;
import com.project.sfoc.entity.teammember.TeamGrant;
import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.teammember.dto.ResponseTeamInfoDto;
import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.user.UserRepository;
import com.project.sfoc.entity.teammember.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public List<ResponseTeamInfoDto> getTeams(Long userId) {

        List<Team> teams = teamRepository.findTeams(userId);
        return teams.stream().map(ResponseTeamInfoDto::from).toList();
    }

    // 팀 이름 중복은 최상위 계층의 이메일로 확인
    public void createTeam(TeamRequestDto teamRequestDto, Long userId) {
        Team team = teamRequestDto.toEntity(createInvitationCode());
        Team savedteam = teamRepository.save(team);

        entryTeam(TeamMemberDto.of(userId, savedteam.getId(), teamRequestDto.userNickname(), HIGHEST_ADMIN));
    }

    public TeamMemberDto entryTeam(TeamMemberDto teamMemberDto) {

        if (teamMemberRepository.existsByTeam_IdAndUser_Id(teamMemberDto.teamId(),teamMemberDto.userId())) {
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
    public void updateTeamInfo(RequestUpdateTeamInfo teamInfoDto, Long teamId, Long userId) {
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


    public Page<ResponseTeamSearchInfoDto> searchTeam(RequestTeamSearchDto teamSearchDto, Pageable pageable) {
        return teamRepository.findSearchResult(teamSearchDto.teamSearch(), pageable);
    }

    private String createInvitationCode() {
        String code = null;
        while(code == null || teamRepository.existsByInvitationCode(code)) {
            String randomUUID = UUID.randomUUID().toString();
            String[] split = randomUUID.split("-");
            code = split[0];
        }

        return code;
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
