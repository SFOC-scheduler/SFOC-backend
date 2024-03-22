package com.project.sfoc.entity.entryrequest;

import com.project.sfoc.entity.Provider;
import com.project.sfoc.entity.entryrequest.dto.ResponseRequestEntryDto;
import com.project.sfoc.entity.team.Disclosure;
import com.project.sfoc.entity.team.Team;
import com.project.sfoc.entity.team.TeamRepository;
import com.project.sfoc.entity.teammember.TeamGrant;
import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.teammember.TeamMemberRepository;
import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.user.UserRepository;
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

    private TeamMember getTeamMember(Team team, TeamGrant teamGrant) {
        return TeamMember.of(getUser(1L), team, teamGrant);
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


    @Test
    @DisplayName("팀 신청자 목록 조회")
    void getRequestEntries_success() {
        //Given
        Team team = getTeam(Disclosure.PUBLIC);
        User user = getUser(1L);
        TeamMember teamMember = getTeamMember(team, HIGHEST_ADMIN);
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
    @DisplayName("팀 신청자 목록 조회 실패")
    void getRequestEntries_fail() {
        //Given
        Team team = getTeam(Disclosure.PUBLIC);
        User user = getUser(1L);
        TeamMember teamMember = getTeamMember(team, NORMAL);

        given(teamMemberRepository.findByTeam_IdAndUser_Id(team.getId(), user.getId())).willReturn(Optional.of(teamMember));

        //When
        entryRequestService.getRequestEntries(team.getId(), user.getId());

        //Then


    }

    @Test
    @DisplayName("팀 신청 수락 테스트")
    void apply() {

    }


    @Test
    @DisplayName("팀 신청 거절 테스트")
    void reject() {

    }



}
