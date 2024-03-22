package com.project.sfoc.entity.team;

import com.project.sfoc.entity.entryrequest.EntryRequest;
import com.project.sfoc.entity.entryrequest.EntryRequestRepository;
import com.project.sfoc.entity.team.dto.*;
import com.project.sfoc.entity.teammember.TeamGrant;
import com.project.sfoc.entity.teammember.TeamMember;
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
    private final EntryRequestRepository entryRequestRepository;

    public List<ResponseTeamInfoDto> getTeams(Long userId) {

        List<Team> teams = teamRepository.findTeams(userId);
        return teams.stream().map(ResponseTeamInfoDto::from).toList();
    }

    // 팀 이름 중복은 최상위 계층의 이메일로 확인
    public void createTeam(RequestTeamDto teamRequestDto, Long userId) {
        Team team = teamRequestDto.toEntity(createInvitationCode());
        Team savedteam = teamRepository.save(team);

        entryTeam(TeamMemberDto.of(userId, savedteam.getId(), HIGHEST_ADMIN));
    }


    public void entryTeam(TeamMemberDto teamMemberDto) {
        if (teamMemberRepository.existsByTeam_IdAndUser_Id(teamMemberDto.teamId(), teamMemberDto.userId())) {
            log.info("이미 팀에 존재합니다.");
            throw new IllegalArgumentException();
        }

        if (entryRequestRepository.existsByTeam_IdAndUser_Id(teamMemberDto.teamId(), teamMemberDto.userId())) {
            log.info("이미 신청한 팀입니다.");
            throw new IllegalArgumentException();
        }

        User user = userRepository.findById(teamMemberDto.userId()).orElseThrow(IllegalArgumentException::new);
        Team team = findTeamByTeamId(teamMemberDto.teamId());

        Disclosure disclosure = team.getDisclosure();

        if (disclosure == Disclosure.PUBLIC) {
            TeamMember teamMember = TeamMember.of(user, team, teamMemberDto.teamGrant());
            teamMemberRepository.save(teamMember);
        } else if (disclosure == Disclosure.APPROVAL) {
            EntryRequest entryRequest = EntryRequest.of(user, team);
            entryRequestRepository.save(entryRequest);

        } else if (disclosure == Disclosure.PRIVATE) {
            log.info("팀에 참가할 수 없습니다.");
            throw new IllegalStateException();
        } else {
            log.info("Disclosure 오류");
            throw new IllegalStateException();
        }

    }


    // 팀 설정을 위한 팀 정보 조회
    public AbstractTeamInfoDto getTeamInfo(Long teamId, Long userId) {
        Team team = findTeamByTeamId(teamId);
        TeamMember teamMember = findTeamMemberByTeamAndUser(teamId, userId);
        TeamGrant teamGrant = teamMember.getTeamGrant();

        return teamGrant.getInfo(team, teamMember);
    }

    // TODO 성능?
    public void updateTeamInfo(UpdateTeamInfo teamInfoDto, Long teamId, Long userId) {

        if (teamMemberRepository.existsByUserNicknameAndTeam_Id(teamInfoDto.userNickname(), teamId)) {
            log.info("닉네임 중복 오류");
            throw new IllegalArgumentException();
        }

        TeamMember teamMember = findTeamMemberByTeamAndUser(teamId, userId);
        TeamGrant teamGrant = teamMember.getTeamGrant();
        Team team = findTeamByTeamId(teamId);

        teamGrant.getInfo(team, teamMember);

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


    private Team findTeamByTeamId(Long teamId) {
            return teamRepository.findById(teamId).orElseThrow(IllegalArgumentException::new);
        }

    private TeamMember findTeamMemberByTeamAndUser(Long teamId, Long userId) {
        return teamMemberRepository.findByTeam_IdAndUser_Id(teamId, userId).orElseThrow(IllegalArgumentException::new);
    }

}
