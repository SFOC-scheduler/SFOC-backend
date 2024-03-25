package com.project.sfoc.entity.team;

import com.project.sfoc.entity.entryrequest.EntryRequest;
import com.project.sfoc.entity.entryrequest.EntryRequestRepository;
import com.project.sfoc.entity.team.dto.*;
import com.project.sfoc.entity.teammember.TeamGrant;
import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.user.UserRepository;
import com.project.sfoc.entity.teammember.TeamMemberRepository;
import com.project.sfoc.exception.EntityNotFoundException;
import com.project.sfoc.exception.Error;
import com.project.sfoc.exception.IllegalDtoException;
import com.project.sfoc.exception.PermissionDeniedError;
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
            throw new IllegalDtoException("이미 팀에 존재합니다.", Error.INVALID_DTO);
        }

        if (entryRequestRepository.existsByTeam_IdAndUser_Id(teamMemberDto.teamId(), teamMemberDto.userId())) {
            throw new IllegalDtoException("이미 신청한 팀입니다.", Error.INVALID_DTO);
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
            throw new PermissionDeniedError(Error.DENIED_ACCESS);
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
    public void updateTeamInfo(RequestUpdateTeamInfo teamInfoDto, Long teamId, Long userId) {

        if (teamMemberRepository.existsByUserNicknameAndTeam_Id(teamInfoDto.userNickname(), teamId)) {
            throw new IllegalDtoException("닉네임이 이미 팀에 존재합니다.", Error.INVALID_DTO);
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
            return teamRepository.findById(teamId).orElseThrow(() -> new EntityNotFoundException(Error.INVALID_DTO));
        }

    private TeamMember findTeamMemberByTeamAndUser(Long teamId, Long userId) {
        return teamMemberRepository.findByTeam_IdAndUser_Id(teamId, userId).orElseThrow(() -> new EntityNotFoundException(Error.INVALID_DTO));
    }

}
