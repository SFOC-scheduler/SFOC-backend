package com.project.sfoc.entity.team;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.project.sfoc.entity.entryrequest.EntryRequest;
import com.project.sfoc.entity.entryrequest.EntryRequestRepository;
import com.project.sfoc.entity.team.dto.*;
import com.project.sfoc.entity.teammember.TeamGrant;
import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.user.UserRepository;
import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.teammember.TeamMemberRepository;
import com.project.sfoc.exception.Error;
import com.project.sfoc.exception.IllegalDtoException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static com.project.sfoc.entity.team.Disclosure.APPROVAL;
import static com.project.sfoc.entity.team.Disclosure.PUBLIC;
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

    @Mock
    private EntryRequestRepository entryRequestRepository;
    @InjectMocks
    private TeamService teamService;


    private User getUser() {
        User user =  userArbitraryBuilder.sample();
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    };

    private Team getTeam(Disclosure disclosure) {
        Team team = teamArbitraryBuilder.sample();
        ReflectionTestUtils.setField(team, "id", 1L);
        ReflectionTestUtils.setField(team, "disclosure", disclosure);
        return team;
    }

    private TeamMember getTeamMember(User user, Team team, TeamGrant teamGrant) {
        return TeamMember.of(user, team, teamGrant);
    }

    private List<Team> getTeams() {

        List<Team> teams = new ArrayList<>();

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

        return teams;
    }

    private final FixtureMonkey fixtureMonkey = FixtureMonkey.builder().
            objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE).build();

    private ArbitraryBuilder<User> userArbitraryBuilder;
    private ArbitraryBuilder<Team> teamArbitraryBuilder;

    @BeforeEach
    public void setup() {
        FixtureMonkey fixtureMonkey = FixtureMonkey.builder().
                objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE).build();

        userArbitraryBuilder = fixtureMonkey.giveMeBuilder(User.class)
                .setNotNull("*")
                .setNull("id");

        teamArbitraryBuilder = fixtureMonkey.giveMeBuilder(Team.class)
                .setNotNull("*")
                .setNull("id");

    }

    @Test
    @DisplayName("팀 생성 테스트")
    public void createTeam_Return_TeamRequestDto() {
        //Given
        User user = getUser();
        Team team = getTeam(PUBLIC);
        RequestTeamDto requestTeamDto =
                new RequestTeamDto("team1", "팀에 대한 설명입니다.", PUBLIC);
        TeamMember teamMember = getTeamMember(user, team, HIGHEST_ADMIN);

        given(teamRepository.existsByInvitationCode(any(String.class))).willReturn(false);
        given(teamRepository.save(any(Team.class))).willReturn(team);
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(teamRepository.findById(team.getId())).willReturn(Optional.of(team));
        given(teamMemberRepository.existsByTeam_IdAndUser_Id(team.getId(), user.getId())).willReturn(false);
        given(teamMemberRepository.save(any(TeamMember.class))).willReturn(teamMember);
        given(entryRequestRepository.existsByTeam_IdAndUser_Id(team.getId(), user.getId())).willReturn(false);

        //When
        teamService.createTeam(requestTeamDto, 1L);

        //Then
        then(teamRepository).should(times(1)).existsByInvitationCode(any());
        then(teamRepository).should(times(1)).save(any(Team.class));
        then(userRepository).should(times(1)).findById(1L);
        then(teamRepository).should(times(1)).findById(1L);
        then(teamMemberRepository).should(times(1)).existsByTeam_IdAndUser_Id(1L, 1L);
        then(teamMemberRepository).should(times(1)).save(any(TeamMember.class));

    }

    @Test
    @DisplayName("팀 생성 실패 테스트")
    public void createTeam_Return_Exception() {
        //Given
        RequestTeamDto requestTeamDto = Mockito.mock(RequestTeamDto.class);

        given(teamRepository.existsByInvitationCode(anyString())).willReturn(false);
        given(requestTeamDto.toEntity(any())).willReturn(null);
        given(teamRepository.save(any())).willThrow(new InvalidDataAccessApiUsageException("엔티티가 null 입니다."));

        //When
        assertThatThrownBy(() -> teamService.createTeam(requestTeamDto, 1L)).hasMessage("엔티티가 null 입니다.");

        //Then
        then(teamRepository).should(times(1)).existsByInvitationCode(any());
        then(requestTeamDto).should(times(1)).toEntity(any());

    }

    @Test
    @DisplayName("팀 참가 테스트 - PUBLIC")
    public void entryTeam_PUBLIC() {
        //Given
        Team team = getTeam(PUBLIC);
        User user = getUser();
        TeamMemberDto teamMemberDto = TeamMemberDto.of(1L, 1L, NORMAL);
        TeamMember teamMember = getTeamMember(user, team, HIGHEST_ADMIN);

        given(teamMemberRepository.existsByTeam_IdAndUser_Id(team.getId(), user.getId())).
                willReturn(false);
        given(entryRequestRepository.existsByTeam_IdAndUser_Id(team.getId(), user.getId())).willReturn(false);
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(teamRepository.findById(team.getId())).willReturn(Optional.of(team));
        given(teamMemberRepository.save(any(TeamMember.class))).willReturn(teamMember);

        //When
        teamService.entryTeam(teamMemberDto);

        //then
        then(teamMemberRepository).should(times(1)).existsByTeam_IdAndUser_Id(1L, 1L);
        then(entryRequestRepository).should(times(1)).existsByTeam_IdAndUser_Id(team.getId(), user.getId());
        then(userRepository).should(times(1)).findById(1L);
        then(teamRepository).should(times(1)).findById(1L);
        then(teamMemberRepository).should(times(1)).save(any(TeamMember.class));

    }

    @Test
    @DisplayName("팀 참가 테스트 - APPROVAL")
    public void entryTeam_APPROVAL() {
        //Given
        Team team = getTeam(APPROVAL);
        User user = getUser();
        TeamMemberDto teamMemberDto = TeamMemberDto.of(1L, 1L, NORMAL);
        EntryRequest entryRequest = EntryRequest.of(user, team);

        given(teamMemberRepository.existsByTeam_IdAndUser_Id(team.getId(), user.getId())).
                willReturn(false);
        given(entryRequestRepository.existsByTeam_IdAndUser_Id(team.getId(), user.getId())).willReturn(false);
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(teamRepository.findById(team.getId())).willReturn(Optional.of(team));
        given(entryRequestRepository.save(any(EntryRequest.class))).willReturn(entryRequest);

        //When
        teamService.entryTeam(teamMemberDto);

        //then
        then(teamMemberRepository).should(times(1)).existsByTeam_IdAndUser_Id(1L, 1L);
        then(entryRequestRepository).should(times(1)).existsByTeam_IdAndUser_Id(team.getId(), user.getId());
        then(userRepository).should(times(1)).findById(1L);
        then(teamRepository).should(times(1)).findById(1L);
        then(entryRequestRepository).should(times(1)).save(any(EntryRequest.class));

    }

    @Test
    @DisplayName("팀 참가 실패 테스트")
    public void entryTeam_Return_Exception() {
        //Given
        TeamMemberDto teamMemberDto = TeamMemberDto.of(1L, 1L, NORMAL);
        Team team = getTeam(PUBLIC);
        User user = getUser();

        given(teamMemberRepository.existsByTeam_IdAndUser_Id(team.getId(), user.getId())).willReturn(true).willReturn(false);
        given(entryRequestRepository.existsByTeam_IdAndUser_Id(team.getId(), user.getId())).willReturn(true);

        //When
        //Then
        Assertions.assertThatThrownBy(() -> teamService.entryTeam(teamMemberDto))
                .isInstanceOf(IllegalDtoException.class)
                .extracting("error")
                .isEqualTo(Error.INVALID_DTO);
        Assertions.assertThatThrownBy(() -> teamService.entryTeam(teamMemberDto))
                .isInstanceOf(IllegalDtoException.class)
                .extracting("error")
                .isEqualTo(Error.INVALID_DTO);
    }

    @Test
    @DisplayName("NORMAL 권한의 구성원이 팀 설정을 위한 팀 정보 조회 테스트")
    public void getTeamInfo_Return_NormalTeamInfoDto() {
        //Given
        Team team = getTeam(PUBLIC);
        User user = getUser();
        TeamMember teamMember = getTeamMember(user, team, NORMAL);

        given(teamRepository.findById(team.getId())).willReturn(Optional.of(team));
        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId())).willReturn(Optional.of(teamMember));

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
    public void getTeamInfo_Throw_Exception() {
        //Given
        Team team = getTeam(PUBLIC);
        User user = getUser();
        TeamMember teamMember = getTeamMember(user, team, NORMAL);

        given(teamRepository.findById(team.getId())).willReturn(Optional.of(team));
        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId())).willReturn(Optional.of(teamMember));

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
    public void getTeamInfo_Return_AdminTeamInfoDto() {
        //Given
        Team team = getTeam(PUBLIC);
        User user = getUser();
        TeamMember teamMember = getTeamMember(user, team, HIGHEST_ADMIN);

        given(teamRepository.findById(team.getId())).willReturn(Optional.of(team));
        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId())).willReturn(Optional.of(teamMember));

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
    public void updateTeamMemberAndTeam_Return_Void() {
        //Given
        RequestUpdateTeamInfo updateTeamInfo = new RequestUpdateTeamInfo("team update", "팀이 변경되었습니다.",
                Disclosure.APPROVAL, "팀 변경 닉네임", "유저 변경 닉네임");

        Team team = getTeam(PUBLIC);
        User user = getUser();
        TeamMember teamMember = getTeamMember(user, team, HIGHEST_ADMIN);
        String userNickname = updateTeamInfo.userNickname();


        given(teamMemberRepository.existsByUserNicknameAndTeam_Id(userNickname, team.getId()))
                .willReturn(false);
        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId()))
                .willReturn(Optional.of(teamMember));
        given(teamRepository.findById(team.getId()))
                .willReturn(Optional.of(team));
        //When
        teamService.updateTeamInfo(updateTeamInfo, 1L, 1L);

        //Then
        then(teamMemberRepository).should(times(1)).
                existsByUserNicknameAndTeam_Id(userNickname, team.getId());
        then(teamMemberRepository).should(times(1)).
                findByTeam_IdAndUser_Id(any(), any());
        then(teamRepository).
                should(times(1)).findById(any());

    }

    // TODO: fixturemonkey 랜덤 값 반환 생각해서 테스트 짜기
    @Test
    @DisplayName("일반 구성원이 팀 정보 업데이트 테스트")
    public void updateTeamMember_Return_Void() {
        //Given

        RequestUpdateTeamInfo updateTeamInfo = new RequestUpdateTeamInfo("team update", "팀이 변경되었습니다.",
                Disclosure.APPROVAL, "팀 변경 닉네임", "유저 변경 닉네임");
        Team team = getTeam(PUBLIC);
        User user = getUser();
        TeamMember teamMember = getTeamMember(user, team, HIGHEST_ADMIN);
        String userNickname = updateTeamInfo.userNickname();


        given(teamMemberRepository.existsByUserNicknameAndTeam_Id(userNickname, team.getId()))
                .willReturn(false);
        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId()))
                .willReturn(Optional.of(teamMember));
        given(teamRepository.findById(team.getId()))
                .willReturn(Optional.of(team));

        //When
        teamService.updateTeamInfo(updateTeamInfo, 1L, 1L);

        //Then
        then(teamMemberRepository).should(times(1)).existsByUserNicknameAndTeam_Id(userNickname, team.getId());
        then(teamMemberRepository).should(times(1)).findByTeam_IdAndUser_Id(any(), any());
        then(teamRepository).should(times(1)).findById(any());

    }


    @Test
    @DisplayName("팀 이름 일정 검색 시 검색 성공")
    public void teamSearch_Return_List_SearchResponseDto() {
        //Given
        RequestTeamSearchDto dto = RequestTeamSearchDto.of("team");
        String search = dto.teamSearch();
        ResponseTeamSearchInfoDto dto1 =
                new ResponseTeamSearchInfoDto(1L, "team1", "this is team1", "abcd@google.com");
        ResponseTeamSearchInfoDto dto2 =
                new ResponseTeamSearchInfoDto(2L, "team2", "this is team2", "abcd@google.com");
        List<ResponseTeamSearchInfoDto> response = new ArrayList<>();
        response.add(dto1);
        response.add(dto2);

        Pageable page = PageRequest.of(0, 10);
        Page<ResponseTeamSearchInfoDto> responseTeamSearchInfoDto = new PageImpl<>(response, page, response.size());

        given(teamRepository.findSearchResult(anyString(), any(Pageable.class))).
                willReturn(responseTeamSearchInfoDto);

        //When
        Page<ResponseTeamSearchInfoDto> teamList = teamService.searchTeam(dto, page);

        //Then
        assertThat(teamList).isNotNull();
        assertThat(teamList.getTotalElements()).isEqualTo(2);


        then(teamRepository).should(times(1)).
                findSearchResult(dto.teamSearch(), page);

    }

    @Test
    @DisplayName("사용자의 소속된 팀 정보 가져오기 성공")
    public void getTeams_Return_List_ResponseTeamInfoDto() {
        //Given
        List<Team> teams = getTeams();
        given(teamRepository.findTeams(any())).willReturn(teams);
        List<ResponseTeamInfoDto> response = teams.stream().map(ResponseTeamInfoDto::from).toList();
        //When
        List<ResponseTeamInfoDto> dto = teamService.getTeams(1L);

        //Then
        assertThat(dto).isNotNull();
        assertThat(dto).isEqualTo(response);

        then(teamRepository).should(times(1)).findTeams(1L);

    }

}