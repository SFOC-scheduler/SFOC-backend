package com.project.sfoc.entity.teammember;

import com.project.sfoc.entity.team.TeamRepository;
import com.project.sfoc.entity.teammember.dto.RequestDeleteTeamMemberDto;
import com.project.sfoc.entity.teammember.dto.RequestUpdateTeamGrantDto;
import com.project.sfoc.entity.teammember.strategy.*;
import com.project.sfoc.entity.user.Provider;
import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.team.Disclosure;
import com.project.sfoc.entity.team.Team;
import com.project.sfoc.entity.teammember.dto.ResponseTeamMemberDto;
import com.project.sfoc.exception.Error;
import com.project.sfoc.exception.IllegalDtoException;
import com.project.sfoc.exception.PermissionDeniedError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.project.sfoc.entity.team.Disclosure.APPROVAL;
import static com.project.sfoc.entity.team.Disclosure.PUBLIC;
import static com.project.sfoc.entity.teammember.TeamGrant.*;
import static com.project.sfoc.entity.teammember.TeamGrant.MIDDLE_ADMIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TeamRepository teamRepository;


    @Mock
    private TeamGrantStrategyProvider provider;
    @InjectMocks
    private TeamMemberService teamMemberService;

    private Team getTeam(Disclosure disclosure) {
        Team team = Team.of("팀1", "1234", "팀1 입니다.", disclosure);
        ReflectionTestUtils.setField(team, "id", 1L);
        return team;
    }

    private User getUser(Long userId) {
        User user = User.of(Provider.GOOGLE, "abcd@google.com" + userId, "1234");
        ReflectionTestUtils.setField(user, "id", userId);
        return user;
    }

    private TeamMember getTeamMember(User user, Team team, TeamGrant teamGrant) {
        TeamMember teamMember = TeamMember.of(user, team, teamGrant);
        ReflectionTestUtils.setField(teamMember,"id", 1L);
        return teamMember;
    }

    private List<TeamMember> getTeamMembers(Team team) {
        List<TeamMember> teamMembers = new ArrayList<>();

        TeamMember teamMember1 = TeamMember.of(getUser(2L), team,  NORMAL);
        TeamMember teamMember2 = TeamMember.of(getUser(3L), team,  NORMAL);
        TeamMember teamMember3 = TeamMember.of(getUser(4L), team,  MIDDLE_ADMIN);

        teamMembers.add((teamMember1));
        teamMembers.add((teamMember2));
        teamMembers.add((teamMember3));

        return teamMembers;
    }


    @Test
    @DisplayName("팀 멤버 리스트 조회 테스트 성공")
    public void findTeamMembers_Success() {
        //Given
        User user = getUser(1L);
        Team team = getTeam(PUBLIC);
        TeamMember teamMember = getTeamMember(user, team, HIGHEST_ADMIN);
        List<TeamMember> teamMembers = getTeamMembers(team);
        teamMembers.add(teamMember);
        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId())).
                willReturn(Optional.of(teamMember));
        given(teamMemberRepository.findByTeam_Id(team.getId())).willReturn(teamMembers);

        //When
        List<ResponseTeamMemberDto> teamMembersDto = teamMemberService.findTeamMembers(1L, 1L);

        //Then

        assertThat(teamMembersDto.size()).isEqualTo(4);
        then(teamMemberRepository).should().findByTeam_IdAndUser_Id(team.getId(), user.getId());
        then(teamMemberRepository).should().findByTeam_Id(team.getId());

    }

    @Test
    @DisplayName("팀 멤버 리스트 조회 테스트 실패 - 권한")
    public void findTeamMembersFailure_Auto() {
        //Given
        User user = getUser(1L);
        Team team = getTeam(PUBLIC);
        TeamMember teamMember = getTeamMember(user, team, NORMAL);
        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId())).
                willReturn(Optional.of(teamMember));

        //When
        //Then
        assertThatThrownBy(() -> teamMemberService.findTeamMembers(team.getId(), user.getId()))
                .isInstanceOf(PermissionDeniedError.class)
                .extracting("error")
                .isEqualTo(Error.DENIED_ACCESS);
    }



    @Test
    @DisplayName("팀 멤버 삭제 성공")
    public void deleteTeamMemberSuccess() {
        //Given
        RequestDeleteTeamMemberDto requestDeleteTeamMemberDto = new RequestDeleteTeamMemberDto(2L);
        Team team = getTeam(PUBLIC);
        User user = getUser(1L);
        TeamMember teamMember = getTeamMember(user, team, HIGHEST_ADMIN);
        TeamMember deleteTeamMember = getTeamMember(getUser(2L), team, NORMAL);
        ReflectionTestUtils.setField(deleteTeamMember,"id", 2L);

        TeamMemberDeleteStrategy strategy = new TeamMemberDeleteHighestAdminStrategy(teamMemberRepository, teamRepository);

        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId()))
                .willReturn(Optional.of(teamMember));
        given(teamMemberRepository.findById(deleteTeamMember.getId())).willReturn(Optional.of(deleteTeamMember));
        given(provider.getDeleteStrategy(teamMember.getTeamGrant())).willReturn(strategy);

        //When
        teamMemberService.deleteTeamMember(requestDeleteTeamMemberDto, team.getId(), user.getId());
        //Then

        then(teamMemberRepository).should().findByTeam_IdAndUser_Id(team.getId(), user.getId());
        then(teamMemberRepository).should().findById(deleteTeamMember.getId());
        then(teamMemberRepository).should().delete(deleteTeamMember);
    }


    @Test
    @DisplayName("팀 멤버 삭제와 팀 삭제")
    public void deleteTeamMemberAndTeamSuccess() {
        //Given
        RequestDeleteTeamMemberDto requestDeleteTeamMemberDto = new RequestDeleteTeamMemberDto(1L);
        Team team = getTeam(PUBLIC);
        User user = getUser(1L);
        TeamMember teamMember = getTeamMember(user, team, HIGHEST_ADMIN);

        TeamMemberDeleteStrategy strategy = new TeamMemberDeleteHighestAdminStrategy(teamMemberRepository, teamRepository);


        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId()))
                .willReturn(Optional.of(teamMember));
        given(teamMemberRepository.findById(teamMember.getId())).willReturn(Optional.of(teamMember));
        given(teamMemberRepository.countByTeam_Id(team.getId())).willReturn(1);
        given(provider.getDeleteStrategy(teamMember.getTeamGrant())).willReturn(strategy);

        //When
        teamMemberService.deleteTeamMember(requestDeleteTeamMemberDto, team.getId(), user.getId());

        //Then
        then(teamMemberRepository).should().findByTeam_IdAndUser_Id(team.getId(), user.getId());
        then(teamMemberRepository).should().findById(teamMember.getId());
        then(teamMemberRepository).should().countByTeam_Id(team.getId());
        then(teamMemberRepository).should().delete(teamMember);
        then(teamRepository).should().deleteById(team.getId());

    }


    @Test
    @DisplayName("팀 멤버 삭제 실패 - 관리자")
    public void deleteTeamMemberFailure_admin() {
        //Given
        RequestDeleteTeamMemberDto dto = new RequestDeleteTeamMemberDto(1L);
        Team team = getTeam(PUBLIC);
        User user = getUser(1L);
        TeamMember teamMember = getTeamMember(user, team, HIGHEST_ADMIN);

        TeamMemberDeleteStrategy strategy = new TeamMemberDeleteHighestAdminStrategy(teamMemberRepository, teamRepository);


        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId()))
                .willReturn(Optional.of(teamMember));
        given(teamMemberRepository.findById(dto.teamMemberId())).willReturn(Optional.of(teamMember));
        given(teamMemberRepository.countByTeam_Id(team.getId())).willReturn(3);
        given(provider.getDeleteStrategy(teamMember.getTeamGrant())).willReturn(strategy);
        //When
        //Then
        assertThatThrownBy(() -> teamMemberService.deleteTeamMember(dto, team.getId(), user.getId()))
                .isInstanceOf(IllegalDtoException.class)
                .hasMessageStartingWith("최상위 관리자는 ")
                .extracting("error")
                .isEqualTo(Error.INVALID_DTO);
    }

    @Test
    @DisplayName("팀 멤버 권한 수정 성공")
    public void updateTeamGrantSuccess() {
        //Given
        RequestUpdateTeamGrantDto dto = new RequestUpdateTeamGrantDto(2L, MIDDLE_ADMIN);
        Team team = getTeam(PUBLIC);
        User user = getUser(1L);
        TeamMember teamMember = getTeamMember(user, team, HIGHEST_ADMIN);
        TeamMember teamMember2 = getTeamMember(getUser(2L), team, NORMAL);
        ReflectionTestUtils.setField(teamMember2,"id", dto.teamMemberId());
        TeamGrantUpdateStrategy strategy = new TeamGrantUpdateHighestAdminStrategy();

        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId()))
                .willReturn(Optional.of(teamMember));
        given(teamMemberRepository.findById(teamMember2.getId())).willReturn(Optional.of(teamMember2));
        given(provider.getUpdateStrategy(teamMember.getTeamGrant())).willReturn(strategy);

        //When
        teamMemberService.updateTeamGrant(dto, team.getId(), user.getId());

        //Then
        then(teamMemberRepository).should().findByTeam_IdAndUser_Id(team.getId(), user.getId());
        then(teamMemberRepository).should().findById(teamMember2.getId());
    }

    @Test
    @DisplayName("팀 멤버 권한 수정 실패 - 다른 팀 접근")
    public void updateTeamGrantFailure_HighestAdmin() {
        //Given
        RequestUpdateTeamGrantDto dto = new RequestUpdateTeamGrantDto(2L, HIGHEST_ADMIN);
        Team team = getTeam(PUBLIC);
        Team team2 = getTeam(APPROVAL);
        ReflectionTestUtils.setField(team2,"id",2L);
        User user = getUser(1L);
        TeamMember teamMember = getTeamMember(user, team, HIGHEST_ADMIN);
        TeamMember teamMember2 = getTeamMember(getUser(2L), team2, NORMAL);
        ReflectionTestUtils.setField(teamMember2,"id", dto.teamMemberId());


        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId()))
                .willReturn(Optional.of(teamMember));
        given(teamMemberRepository.findById(dto.teamMemberId())).willReturn(Optional.of(teamMember2));


        //When
        //Then
        assertThatThrownBy(() -> teamMemberService.updateTeamGrant(dto, team.getId(), user.getId()))
                .isInstanceOf(IllegalDtoException.class)
                .extracting("error")
                .isEqualTo(Error.INVALID_DTO);
    }

}