package com.project.sfoc.team;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
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
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static com.project.sfoc.teammember.TeamGrant.HIGHEST_ADMIN;
import static com.project.sfoc.teammember.TeamGrant.NORMAL;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

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

    private ArbitraryBuilder<User> userArbitraryBuilder;
    private ArbitraryBuilder<Team> teamArbitraryBuilder;

    @BeforeEach
    public void setup() {
        FixtureMonkey fixtureMonkey = FixtureMonkey.builder().
                objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE).build();

        userArbitraryBuilder = fixtureMonkey.giveMeBuilder(User.class).setNotNull("*").
                setNull("id");

        teamArbitraryBuilder = fixtureMonkey.giveMeBuilder(Team.class).setNotNull("*").
                setNull("id");

        user = userArbitraryBuilder.sample();
        team = teamArbitraryBuilder.sample();

    }


    @Test
    @DisplayName("팀 생성 테스트")
    public void teamService_CreateTeam_Return_TeamRequestDto() {
        //Given
        TeamRequestDto teamRequestDto = new TeamRequestDto("team1", "팀에 대한 설명입니다.", Disclosure.PUBLIC, "abc");
        TeamMember teamMember = TeamMember.of("팀 닉네임", "유저 닉네임", HIGHEST_ADMIN, user, team);

        ReflectionTestUtils.setField(team, "id", 1L);
        ReflectionTestUtils.setField(user, "id", 1L);


        given(teamRepository.existsByInvitationCode(any(String.class))).willReturn(false);
        given(teamRepository.save(any(Team.class))).willReturn(team);
        given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
        given(teamRepository.findById(any(Long.class))).willReturn(Optional.of(team));
        given(teamMemberRepository.existsByTeam_IdAndUser_Id(any(Long.class), any(Long.class))).willReturn(false);
        given(teamMemberRepository.existsByUserNicknameAndTeam_Id(any(String.class), any(Long.class))).willReturn(false);
        given(teamMemberRepository.save(any(TeamMember.class))).willReturn(teamMember);

        //When
        teamService.createTeam(teamRequestDto, 1L);


        //Then
        then(teamRepository).should(times(1)).existsByInvitationCode(any());
        then(teamRepository).should(times(1)).save(any(Team.class));
        then(userRepository).should(times(1)).findById(1L);
        then(teamRepository).should(times(1)).findById(1L);
        then(teamMemberRepository).should(times(1)).existsByTeam_IdAndUser_Id(1L, 1L);
        then(teamMemberRepository).should(times(1)).existsByUserNicknameAndTeam_Id("abc", 1L);
        then(teamMemberRepository).should(times(1)).save(any(TeamMember.class));

    }

    @Test
    @DisplayName("팀 생성 실패 테스트")
    public void teamService_CreateTeam_Return_Exception() {
        //Given
        TeamRequestDto teamRequestDto = Mockito.mock(TeamRequestDto.class);
        team = null;

        given(teamRepository.existsByInvitationCode(anyString())).willReturn(false);
        given(teamRequestDto.toEntity(any())).willReturn(null);
        given(teamRepository.save(team)).willThrow(new InvalidDataAccessApiUsageException("엔티티가 null 입니다."));

        //When
        assertThatThrownBy(() -> teamService.createTeam(teamRequestDto, 1L)).hasMessage("엔티티가 null 입니다.");

        //Then
        then(teamRepository).should(times(1)).existsByInvitationCode(any());
        then(teamRequestDto).should(times(1)).toEntity(any());

    }

    @Test
    @DisplayName("팀 참가 테스트")
    public void teamService_EntryTeam_Return_TeamMemberDto() {
        //Given
        TeamMemberDto teamMemberDto = TeamMemberDto.of(1L, 1L, "abc", NORMAL);
        TeamMember teamMember = TeamMember.of("팀 닉네임", "유저 닉네임", NORMAL, user, team);
        ReflectionTestUtils.setField(team, "id", 1L);
        ReflectionTestUtils.setField(user, "id", 1L);


        given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
        given(teamRepository.findById(any(Long.class))).willReturn(Optional.of(team));
        given(teamMemberRepository.existsByTeam_IdAndUser_Id(any(Long.class), any(Long.class))).willReturn(false);
        given(teamMemberRepository.existsByUserNicknameAndTeam_Id(any(String.class), any(Long.class))).willReturn(false);
        given(teamMemberRepository.save(any(TeamMember.class))).willReturn(teamMember);

        //When
        TeamMemberDto returnDto = teamService.entryTeam(teamMemberDto);

        //then
        assertThat(returnDto).isNotNull();
        assertThat(returnDto.userNickname()).isEqualTo("abc");
        assertThat(returnDto.teamGrant()).isEqualTo(NORMAL);
        then(userRepository).should(times(1)).findById(any());
        then(teamRepository).should(times(1)).findById(1L);
        then(teamMemberRepository).should(times(1)).existsByTeam_IdAndUser_Id(1L, 1L);
        then(teamMemberRepository).should(times(1)).existsByUserNicknameAndTeam_Id("abc", 1L);
        then(teamMemberRepository).should(times(1)).save(any(TeamMember.class));

    }

    @Test
    @DisplayName("팀 참가 실패 테스트")
    public void teamService_EntryTeam_Return_Exception() {
        //Given
        TeamMemberDto teamMemberDto = TeamMemberDto.of(1L, 1L, "abc", NORMAL);
        TeamMember teamMember = TeamMember.of("팀 닉네임", "유저 닉네임", NORMAL, user, team);
        ReflectionTestUtils.setField(team, "id", 1L);
        ReflectionTestUtils.setField(user, "id", 1L);

        given(teamMemberRepository.existsByTeam_IdAndUser_Id(any(), any())).willReturn(true).willReturn(false);
        given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
        given(teamRepository.findById(any(Long.class))).willReturn(Optional.of(team));
        given(teamMemberRepository.existsByUserNicknameAndTeam_Id(any(String.class), any(Long.class))).willReturn(true);
//        given(teamMemberRepository.save(any(TeamMember.class))).willReturn(teamMember);

        //When
        assertThatThrownBy(() -> teamService.entryTeam(teamMemberDto)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> teamService.entryTeam(teamMemberDto)).isInstanceOf(IllegalArgumentException.class);
        //Then

    }

    @Test
    @DisplayName("NORMAL 권한의 구성원이 팀 설정을 위한 팀 정보 조회 테스트")
    public void teamService_GetTeamInfo_Return_NormalTeamInfoDto() {
        //Given
        TeamMember teamMember = TeamMember.of("팀 닉네임", "유저 닉네임", NORMAL, user, team);

        given(teamRepository.findById(any(Long.class))).willReturn(Optional.of(team));
        given(teamMemberRepository.findByTeam_IdAndUser_Id(any(Long.class), any(Long.class))).willReturn(Optional.of(teamMember));

        //When
        AbstractTeamInfoDto teamInfo = teamService.getTeamInfo(1L, 1L);

        //Then
        assertThat(teamInfo).isNotNull();
        assertThat(teamInfo.getTeamName()).isNull();
        assertThat(teamInfo.getTeamNickname()).isEqualTo(teamMember.getTeamNickname());
        then(teamRepository).should(times(1)).findById(any());
        then(teamMemberRepository).should(times(1)).findByTeam_IdAndUser_Id(any(), any());

    }

    @Test
    @DisplayName("NORMAL 권한의 구성원이 팀 설정을 위한 팀 정보 조회 실패")
    public void teamService_GetTeamInfo_Throw_Exception() {
        //Given
        TeamMember teamMember = TeamMember.of("팀 닉네임", "유저 닉네임", NORMAL, user, team);

        given(teamRepository.findById(any(Long.class))).willReturn(Optional.of(team));
        given(teamMemberRepository.findByTeam_IdAndUser_Id(any(Long.class), any(Long.class))).willReturn(Optional.of(teamMember));

        //When
        AbstractTeamInfoDto teamInfo = teamService.getTeamInfo(1L, 1L);

        //Then
        assertThat(teamInfo).isNotNull();
        assertThat(teamInfo.getTeamName()).isNull();
        assertThat(teamInfo.getTeamNickname()).isEqualTo(teamMember.getTeamNickname());
        then(teamRepository).should(times(1)).findById(any());
        then(teamMemberRepository).should(times(1)).findByTeam_IdAndUser_Id(any(), any());

    }


    @Test
    @DisplayName("관리자가 팀 설정을 위한 팀 정보 조회 테스트")
    public void teamService_GetTeamInfo_Return_AdminTeamInfoDto() {
        //Given
        TeamMember teamMember = TeamMember.of("팀 닉네임", "유저 닉네임", HIGHEST_ADMIN, user, team);

        given(teamRepository.findById(any(Long.class))).willReturn(Optional.of(team));
        given(teamMemberRepository.findByTeam_IdAndUser_Id(any(Long.class), any(Long.class))).willReturn(Optional.of(teamMember));

        //When
        AbstractTeamInfoDto teamInfo = teamService.getTeamInfo(1L, 1L);

        //Then
        assertThat(teamInfo).isNotNull();
        assertThat(teamInfo.getTeamName()).isNotNull();
        assertThat(teamInfo.getTeamName()).isEqualTo(team.getName());
        assertThat(teamInfo.getTeamNickname()).isEqualTo(teamMember.getTeamNickname());
        then(teamRepository).should(times(1)).findById(any());
        then(teamMemberRepository).should(times(1)).findByTeam_IdAndUser_Id((any()), any());

    }


    @Test
    @DisplayName("관리자가 팀 정보 업데이트 테스트")
    public void teamService_UpdateTeamMemberAndTeam_Return_Void() {
        //Given
        UpdateTeamInfo updateTeamInfo = new UpdateTeamInfo("team update", "팀이 변경되었습니다.",
                Disclosure.APPROVAL, "팀 변경 닉네임", "유저 변경 닉네임");

        TeamMember teamMember = TeamMember.of("팀 닉네임", "유저 닉네임", HIGHEST_ADMIN, user, team);

        given(teamMemberRepository.findByTeam_IdAndUser_Id(any(Long.class), any(Long.class))).willReturn(Optional.of(teamMember));
        given(teamRepository.findById(any(Long.class))).willReturn(Optional.of(team));

        //When
        teamService.updateTeamInfo(updateTeamInfo, 1L, 1L);

        //Then
        assertThat(team.getDescription()).isEqualTo(updateTeamInfo.description());
        assertThat(team.getName()).isEqualTo(updateTeamInfo.teamName());
        assertThat(team.getDisclosure()).isEqualTo(updateTeamInfo.disclosure());
        assertThat(teamMember.getTeamNickname()).isEqualTo(updateTeamInfo.teamNickname());
        assertThat(teamMember.getUserNickname()).isEqualTo(updateTeamInfo.userNickname());
        then(teamMemberRepository).should(times(1)).findByTeam_IdAndUser_Id(any(), any());
        then(teamRepository).should(times(1)).findById(any());

    }

    @Test
    @DisplayName("일반 구성원이 팀 정보 업데이트 테스트")
    public void teamService_UpdateTeamMember_Return_Void() {
        //Given
        UpdateTeamInfo updateTeamInfo = new UpdateTeamInfo("team update", "팀이 변경되었습니다.",
                Disclosure.APPROVAL, "팀 변경 닉네임", "유저 변경 닉네임");
        TeamMember teamMember = TeamMember.of("팀 닉네임", "유저 닉네임", NORMAL, user, team);

        given(teamMemberRepository.findByTeam_IdAndUser_Id(any(Long.class), any(Long.class))).willReturn(Optional.of(teamMember));

        //When
        teamService.updateTeamInfo(updateTeamInfo, 1L, 1L);

        //Then
        assertThat(team.getDescription()).isNotEqualTo(updateTeamInfo.description());
        assertThat(team.getName()).isNotEqualTo(updateTeamInfo.teamName());
        assertThat(team.getDisclosure()).isNotEqualTo(updateTeamInfo.disclosure());
        assertThat(teamMember.getTeamNickname()).isEqualTo(updateTeamInfo.teamNickname());
        assertThat(teamMember.getUserNickname()).isEqualTo(updateTeamInfo.userNickname());
        then(teamMemberRepository).should(times(1)).findByTeam_IdAndUser_Id(any(), any());

    }

}