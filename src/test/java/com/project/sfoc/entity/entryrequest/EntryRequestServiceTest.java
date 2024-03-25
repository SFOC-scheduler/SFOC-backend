package com.project.sfoc.entity.entryrequest;

import com.project.sfoc.entity.entryrequest.dto.RequestDeleteTeamRequestDto;
import com.project.sfoc.entity.entryrequest.dto.ResponseRequestEntryDto;
import com.project.sfoc.entity.team.Disclosure;
import com.project.sfoc.entity.team.Team;
import com.project.sfoc.entity.team.TeamRepository;
import com.project.sfoc.entity.teammember.TeamGrant;
import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.teammember.TeamMemberRepository;
import com.project.sfoc.entity.user.Provider;
import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.user.UserRepository;
import com.project.sfoc.exception.EntityNotFoundException;
import com.project.sfoc.exception.Error;
import com.project.sfoc.exception.PermissionDeniedError;
import org.junit.jupiter.api.Assertions;
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

import static com.project.sfoc.entity.teammember.TeamGrant.HIGHEST_ADMIN;
import static com.project.sfoc.entity.teammember.TeamGrant.NORMAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class EntryRequestServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    TeamMemberRepository teamMemberRepository;

    @Mock
    TeamRepository teamRepository;

    @Mock
    EntryRequestRepository entryRequestRepository;

    @InjectMocks
    EntryRequestService entryRequestService;

    private Team getTeam(Disclosure disclosure) {
        Team team = Team.of("팀1", "1234", "팀1 입니다.", disclosure);
        ReflectionTestUtils.setField(team, "id", 1L);
        return team;
    }

    private User getUser(Long id) {
        User user = User.of(Provider.GOOGLE, "abcd@google.com", "1234");
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private TeamMember getTeamMember(User user, Team team, TeamGrant teamGrant) {
        return TeamMember.of(user, team, teamGrant);
    }

    private List<User> getUsers() {

        List<User> users = new ArrayList<>();

        User user1 = getUser(2L);
        User user2 = getUser(3L);
        User user3 = getUser(4L);
        users.add(user1);
        users.add(user2);
        users.add(user3);

        return users;
    }

    private EntryRequest getEntryRequest(User user, Team team) {
        return EntryRequest.of(user, team);
    }


    @Test
    @DisplayName("팀 신청자 목록 조회")
    void getRequestEntries_success() {
        //Given
        Team team = getTeam(Disclosure.PUBLIC);
        User user = getUser(1L);
        TeamMember teamMember = getTeamMember(user, team, HIGHEST_ADMIN);
        List<User> users = getUsers();

        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId())).willReturn(Optional.of(teamMember));
        given(userRepository.findRequestEntries(team.getId())).willReturn(users);

        //When
        List<ResponseRequestEntryDto> requestEntries = entryRequestService.getRequestEntries(user.getId(), team.getId());

        //Then
        assertThat(requestEntries).isNotNull();
        assertThat(requestEntries.size()).isEqualTo(3);

        then(teamMemberRepository).should().findByTeam_IdAndUser_Id(team.getId(), user.getId());
        then(userRepository).should().findRequestEntries(team.getId());

    }

    @Test
    @DisplayName("팀 신청자 목록 조회 실패 - 팀 권한 없음")
    void getRequestEntries_fail() {
        //Given
        Team team = getTeam(Disclosure.PUBLIC);
        User user = getUser(1L);
        TeamMember teamMember = getTeamMember(user, team, NORMAL);

        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId())).willReturn(Optional.of(teamMember));

        //When
        //Then
        assertThatThrownBy(() -> entryRequestService.getRequestEntries(team.getId(), user.getId()))
                .isInstanceOf(PermissionDeniedError.class)
                .extracting("error").isEqualTo(Error.DENIED_ACCESS);

    }

    @Test
    @DisplayName("팀 신청 수락 테스트")
    void apply() {
        //Given
        RequestDeleteTeamRequestDto dto = new RequestDeleteTeamRequestDto(1L, 2L, true);
        Team team = getTeam(Disclosure.PUBLIC);
        User user = getUser(dto.userId());
        User admin = getUser(1L);
        TeamMember teamMember = getTeamMember(user, team, HIGHEST_ADMIN);
        EntryRequest entryRequest = getEntryRequest(user, team);


        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), admin.getId())).willReturn(Optional.of(teamMember));
        given(entryRequestRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId())).willReturn(Optional.of(entryRequest));
        given(teamRepository.findById(team.getId())).willReturn(Optional.of(team));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(teamMemberRepository.save(any(TeamMember.class))).willReturn(teamMember);

        //When
        entryRequestService.applyOrRequest(admin.getId(), dto);

        //Then
        assertThat(dto.apply()).isEqualTo(true);

        then(teamMemberRepository).should().findByTeam_IdAndUser_Id(team.getId(), admin.getId());
        then(entryRequestRepository).should().findByTeam_IdAndUser_Id(team.getId(), user.getId());
        then(entryRequestRepository).should().delete(entryRequest);
        then(teamRepository).should().findById(team.getId());
        then(userRepository).should().findById(user.getId());
        then(teamMemberRepository).should().save(any(TeamMember.class));

    }


    @Test
    @DisplayName("팀 신청 수락 실패 - 조회 실패")
    void applyFailure() {
        //Given
        RequestDeleteTeamRequestDto dto = new RequestDeleteTeamRequestDto(1L, 2L, false);
        Team team = getTeam(Disclosure.PUBLIC);
        User user = getUser(dto.userId());
        User admin = getUser(1L);
        TeamMember teamMember = getTeamMember(user, team, HIGHEST_ADMIN);
        EntryRequest entryRequest = getEntryRequest(user, team);


        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), admin.getId())).willReturn(Optional.of(teamMember));
        given(entryRequestRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId())).willReturn(Optional.empty());

        //When
        //Then
        assertThatThrownBy(() -> entryRequestService.applyOrRequest(admin.getId(), dto))
                .isInstanceOf(EntityNotFoundException.class)
                .extracting("error")
                .isEqualTo(Error.INVALID_DTO);
    }

    @Test
    @DisplayName("팀 신청 거절 테스트")
    void reject() {
        //Given
        RequestDeleteTeamRequestDto dto = new RequestDeleteTeamRequestDto(1L, 2L, false);
        Team team = getTeam(Disclosure.PUBLIC);
        User user = getUser(dto.userId());
        User admin = getUser(1L);
        TeamMember teamMember = getTeamMember(user, team, HIGHEST_ADMIN);
        EntryRequest entryRequest = getEntryRequest(user, team);


        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), admin.getId())).willReturn(Optional.of(teamMember));
        given(entryRequestRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId())).willReturn(Optional.of(entryRequest));

        //When
        entryRequestService.applyOrRequest(admin.getId(), dto);

        //Then
        assertThat(dto.apply()).isEqualTo(false);

        then(teamMemberRepository).should().findByTeam_IdAndUser_Id(team.getId(), admin.getId());
        then(entryRequestRepository).should().findByTeam_IdAndUser_Id(team.getId(), user.getId());
        then(entryRequestRepository).should().delete(entryRequest);
    }




}
