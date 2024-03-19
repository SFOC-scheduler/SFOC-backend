package com.project.sfoc.team;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.project.sfoc.entity.Provider;
import com.project.sfoc.entity.team.Disclosure;
import com.project.sfoc.entity.team.Team;
import com.project.sfoc.entity.team.TeamRepository;
import com.project.sfoc.entity.team.TeamService;
import com.project.sfoc.entity.team.dto.*;
import com.project.sfoc.entity.teammember.dto.ResponseTeamInfoDto;
import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.user.UserRepository;
import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.teammember.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.project.sfoc.entity.teammember.TeamGrant.HIGHEST_ADMIN;
import static com.project.sfoc.entity.teammember.TeamGrant.NORMAL;
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

    private List<Team> teams = new ArrayList<>();

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

        Team team1 = Team.of("team1", "1234", "팀에 대한 설명입니다1.", Disclosure.PUBLIC);
        Team team2 = Team.of("team12", "12345", "팀에 대한 설명입니다2.", Disclosure.PUBLIC);
        Team team3 = Team.of("team123", "123456", "팀에 대한 설명입니다3.", Disclosure.PUBLIC);
        Team team4 = Team.of("team1234", "1234567", "팀에 대한 설명입니다4.", Disclosure.PUBLIC);
        Team team5 = Team.of("team12345", "12345678", "팀에 대한 설명입니다5.", Disclosure.PUBLIC);

        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
        teams.add(team4);
        teams.add(team5);
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
        ReflectionTestUtils.setField(team, "id", 1L);
        ReflectionTestUtils.setField(user, "id", 1L);

        given(teamMemberRepository.existsByTeam_IdAndUser_Id(any(), any())).willReturn(true).willReturn(false);
        given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
        given(teamRepository.findById(any(Long.class))).willReturn(Optional.of(team));
        given(teamMemberRepository.existsByUserNicknameAndTeam_Id(any(String.class), any(Long.class))).willReturn(true);

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


    @DisplayName("관리자가 팀 정보 업데이트 테스트")
    @RepeatedTest(value = 5)
    public void teamService_UpdateTeamMemberAndTeam_Return_Void() {
        //Given

        RequestUpdateTeamInfo requestUpdateTeamInfo = new RequestUpdateTeamInfo("team update", "팀이 변경되었습니다.",
                Disclosure.APPROVAL, "팀 변경 닉네임", "유저 변경 닉네임");

        TeamMember teamMember = TeamMember.of("팀 닉네임", "유저 닉네임", HIGHEST_ADMIN, user, team);

        given(teamMemberRepository.findByTeam_IdAndUser_Id(any(Long.class), any(Long.class))).willReturn(Optional.of(teamMember));
        given(teamRepository.findById(any(Long.class))).willReturn(Optional.of(team));

        //When
        teamService.updateTeamInfo(requestUpdateTeamInfo, 1L, 1L);

        //Then
        assertThat(team.getDescription()).isEqualTo(requestUpdateTeamInfo.description());
        assertThat(team.getName()).isEqualTo(requestUpdateTeamInfo.teamName());
        assertThat(team.getDisclosure()).isEqualTo(requestUpdateTeamInfo.disclosure());
        assertThat(teamMember.getTeamNickname()).isEqualTo(requestUpdateTeamInfo.teamNickname());
        assertThat(teamMember.getUserNickname()).isEqualTo(requestUpdateTeamInfo.userNickname());

        then(teamMemberRepository).should(times(1)).findByTeam_IdAndUser_Id(any(), any());
        then(teamRepository).should(times(1)).findById(any());

    }

    // TODO: fixturemonkey 랜덤 값 반환 생각해서 테스트 짜기
    @DisplayName("일반 구성원이 팀 정보 업데이트 테스트")
    @RepeatedTest(value = 5)
    public void teamService_UpdateTeamMember_Return_Void() {
        //Given

        team = Team.of("team1", "1234", "팀에 대한 설명입니다.", Disclosure.PUBLIC);
        user = User.of(Provider.GOOGLE, "abcd@gmail.com", "12345678901234567890");

        RequestUpdateTeamInfo requestUpdateTeamInfo = new RequestUpdateTeamInfo("team update", "팀이 변경되었습니다.",
                Disclosure.APPROVAL, "팀 변경 닉네임", "유저 변경 닉네임");
        TeamMember teamMember = TeamMember.of("팀 닉네임", "유저 닉네임", NORMAL, user, team);

        given(teamMemberRepository.findByTeam_IdAndUser_Id(any(Long.class), any(Long.class))).willReturn(Optional.of(teamMember));

        //When
        teamService.updateTeamInfo(requestUpdateTeamInfo, 1L, 1L);

        //Then
        assertThat(team.getDescription()).isNotEqualTo(requestUpdateTeamInfo.description());
        assertThat(team.getName()).isNotEqualTo(requestUpdateTeamInfo.teamName());
        assertThat(team.getDisclosure()).isNotEqualTo(requestUpdateTeamInfo.disclosure());
        assertThat(teamMember.getTeamNickname()).isEqualTo(requestUpdateTeamInfo.teamNickname());
        assertThat(teamMember.getUserNickname()).isEqualTo(requestUpdateTeamInfo.userNickname());

        then(teamMemberRepository).should(times(1)).findByTeam_IdAndUser_Id(any(), any());

    }

    @Test
    @DisplayName("팀 이름 일정 검색 시 검색 성공")
    public void teamService_teamSearch_Return_List_SearchResponseDto() {
        //Given
        RequestTeamSearchDto requestTeamSearchDto = RequestTeamSearchDto.of("team");
        ResponseTeamSearchInfoDto responseTeamSearchInfoDto1 = new ResponseTeamSearchInfoDto(1L, "team1", "this is team1", "abcd@google.com");
        ResponseTeamSearchInfoDto responseTeamSearchInfoDto2 = new ResponseTeamSearchInfoDto(2L, "team2", "this is team2", "abcd@google.com");
        List<ResponseTeamSearchInfoDto> response = new ArrayList<>();
        response.add(responseTeamSearchInfoDto1);
        response.add(responseTeamSearchInfoDto2);
        Pageable page = PageRequest.of(0, 10);
        Page<ResponseTeamSearchInfoDto> responseTeamSearchInfoDto = new PageImpl<>(response, page, response.size());

        given(teamRepository.findSearchResult(any(), any())).willReturn(responseTeamSearchInfoDto);

        //When
        Page<ResponseTeamSearchInfoDto> teamList = teamService.searchTeam(requestTeamSearchDto, page);

        //Then
        assertThat(teamList).isNotNull();
        assertThat(teamList.getTotalElements()).isEqualTo(2);


        then(teamRepository).should(times(1)).
                findSearchResult(requestTeamSearchDto.teamSearch(), page);

    }

    @Test
    @DisplayName("사용자의 소속된 팀 정보 가져오기 성공")
    public void teamService_getTeams_Return_List_ResponseTeamInfoDto() {
        //Given
        given(teamRepository.findTeams(any())).willReturn(teams);
        List<ResponseTeamInfoDto> response = teams.stream().map(ResponseTeamInfoDto::from).toList();
        //When
        List<ResponseTeamInfoDto> teams = teamService.getTeams(1L);

        //Then
        assertThat(teams).isNotNull();
        assertThat(teams).isEqualTo(response);

        then(teamRepository).should(times(1)).findTeams(1L);


    }

}