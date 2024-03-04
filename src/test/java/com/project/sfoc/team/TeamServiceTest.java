package com.project.sfoc.team;

import com.project.sfoc.entity.Provider;
import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.user.UserRepository;
import com.project.sfoc.team.dto.AbstractTeamInfoDto;
import com.project.sfoc.team.dto.TeamMemberDto;
import com.project.sfoc.team.dto.TeamRequestDto;
import com.project.sfoc.team.dto.UpdateTeamInfo;
import com.project.sfoc.teammember.TeamMember;
import com.project.sfoc.teammember.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static com.project.sfoc.teammember.TeamGrant.HIGHEST_ADMIN;
import static com.project.sfoc.teammember.TeamGrant.NORMAL;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private TeamService teamService;

    private Team team;
    private User user;

    @BeforeEach
    public void setup() {

        team = Team.of("team1", "1234", "팀에 대한 설명입니다.", Disclosure.PUBLIC);
        user = User.of(Provider.GOOGLE, "abcd@gmail.com", "12345678901234567890");
    }


    @Test
    @DisplayName("팀 생성 테스트")
    public void teamService_CreateTeam_Return_TeamRequestDto() {

        TeamRequestDto teamRequestDto = new TeamRequestDto("team1", "팀에 대한 설명입니다.", Disclosure.PUBLIC, "abc");
        TeamMember teamMember = TeamMember.of("팀 닉네임", "유저 닉네임", NORMAL, user, team);
        TeamMemberDto teamMemberDto = TeamMemberDto.of(1L, 1L, "abc", NORMAL);

        ReflectionTestUtils.setField(team, "id", 1L);


        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(teamRepository.findById(any(Long.class))).thenReturn(Optional.of(team));
        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(teamMember);

        teamService.createTeam(teamRequestDto, 1L);

        verify(teamRepository, never()).save(team);
        verify(userRepository).findById(1L);
        verify(teamRepository).findById(1L);
        verify(teamMemberRepository, never()).save(teamMember);

    }

    @Test()
    @DisplayName("팀 참가 테스트")
    public void teamService_EntryTeam_ReturnTeamMemberDto() {

        TeamMemberDto teamMemberDto = TeamMemberDto.of(1L, 1L, "abc", NORMAL);

        TeamMember teamMember = TeamMember.of("팀 닉네임", "유저 닉네임", NORMAL, user, team);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(teamRepository.findById(any(Long.class))).thenReturn(Optional.of(team));
        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(teamMember);

        TeamMemberDto returnDto = teamService.entryTeam(teamMemberDto);

        assertThat(returnDto).isNotNull();
        assertThat(returnDto.userNickname()).isEqualTo("abc");
        assertThat(returnDto.teamGrant()).isEqualTo(NORMAL);

    }

    @Test
    @DisplayName("NORMAL 권한의 구성원이 팀 설정을 위한 팀 정보 조회 테스트")
    public void teamService_GetTeamInfo_Return_NormalTeamInfoDto() {
        TeamMember teamMember = TeamMember.of("팀 닉네임", "유저 닉네임", NORMAL, user, team);

        when(teamRepository.findById(any(Long.class))).thenReturn(Optional.of(team));
        when(teamMemberRepository.
                findByTeam_IdAndUser_Id(any(Long.class), any(Long.class))).thenReturn(Optional.of(teamMember));

        AbstractTeamInfoDto teamInfo = teamService.getTeamInfo(1L, 1L);

        assertThat(teamInfo).isNotNull();
        assertThat(teamInfo.getTeamName()).isNull();
        assertThat(teamInfo.getTeamNickname()).isEqualTo(teamMember.getTeamNickname());

    }

    @Test
    @DisplayName("관리자가 팀 설정을 위한 팀 정보 조회 테스트")
    public void teamService_GetTeamInfo_Return_AdminTeamInfoDto() {
        TeamMember teamMember = TeamMember.of("팀 닉네임", "유저 닉네임", HIGHEST_ADMIN, user, team);

        when(teamRepository.findById(any(Long.class))).thenReturn(Optional.of(team));
        when(teamMemberRepository.
                findByTeam_IdAndUser_Id(any(Long.class), any(Long.class))).thenReturn(Optional.of(teamMember));

        AbstractTeamInfoDto teamInfo = teamService.getTeamInfo(1L, 1L);

        assertThat(teamInfo).isNotNull();
        assertThat(teamInfo.getTeamName()).isNotNull();
        assertThat(teamInfo.getTeamName()).isEqualTo(team.getName());
        assertThat(teamInfo.getTeamNickname()).isEqualTo(teamMember.getTeamNickname());

    }


    @Test
    @DisplayName("관리자가 팀 정보 업데이트 테스트")
    public void teamService_UpdateTeamMemberAndTeam_Return_Void() {

        UpdateTeamInfo updateTeamInfo = new UpdateTeamInfo("team update", "팀이 변경되었습니다.",
                Disclosure.APPROVAL, "팀 변경 닉네임", "유저 변경 닉네임");

        TeamMember teamMember = TeamMember.of("팀 닉네임", "유저 닉네임", HIGHEST_ADMIN, user, team);

        when(teamMemberRepository.findByTeam_IdAndUser_Id(any(Long.class), any(Long.class))).thenReturn(Optional.of(teamMember));
        when(teamRepository.findById(any(Long.class))).thenReturn(Optional.of(team));

        teamService.updateTeamInfo(updateTeamInfo, 1L, 1L);

        assertThat(team.getDescription()).isEqualTo(updateTeamInfo.description());
        assertThat(team.getName()).isEqualTo(updateTeamInfo.teamName());
        assertThat(team.getDisclosure()).isEqualTo(updateTeamInfo.disclosure());
        assertThat(teamMember.getTeamNickname()).isEqualTo(updateTeamInfo.teamNickname());
        assertThat(teamMember.getUserNickname()).isEqualTo(updateTeamInfo.userNickname());

    }

    @Test
    @DisplayName("일반 구성원이 팀 정보 업데이트 테스트")
    public void teamService_UpdateTeamMember_Return_Void() {

        UpdateTeamInfo updateTeamInfo = new UpdateTeamInfo("team update", "팀이 변경되었습니다.",
                Disclosure.APPROVAL, "팀 변경 닉네임", "유저 변경 닉네임");

        TeamMember teamMember = TeamMember.of("팀 닉네임", "유저 닉네임", NORMAL, user, team);

        when(teamMemberRepository.findByTeam_IdAndUser_Id(any(Long.class), any(Long.class))).thenReturn(Optional.of(teamMember));

        teamService.updateTeamInfo(updateTeamInfo, 1L, 1L);

        assertThat(team.getDescription()).isNotEqualTo(updateTeamInfo.description());
        assertThat(team.getName()).isNotEqualTo(updateTeamInfo.teamName());
        assertThat(team.getDisclosure()).isNotEqualTo(updateTeamInfo.disclosure());
        assertThat(teamMember.getTeamNickname()).isEqualTo(updateTeamInfo.teamNickname());
        assertThat(teamMember.getUserNickname()).isEqualTo(updateTeamInfo.userNickname());

    }

}