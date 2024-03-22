package com.project.sfoc.entity.teammember;

import com.project.sfoc.entity.Provider;
import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.teammember.TeamMemberRepository;
import com.project.sfoc.entity.teammember.TeamMemberService;
import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.team.Disclosure;
import com.project.sfoc.entity.team.Team;
import com.project.sfoc.entity.teammember.dto.TeamMemberResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.project.sfoc.entity.teammember.TeamGrant.*;
import static com.project.sfoc.entity.teammember.TeamGrant.MIDDLE_ADMIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private TeamMemberService teamMemberService;

    private Team team;
    private User user;

    private List<TeamMember> teamMembers = new ArrayList<>();

    @BeforeEach

    public void setup() {
        team = Team.of("team1", "1234", "팀에 대한 설명입니다.", Disclosure.PUBLIC);
        user = User.of(Provider.GOOGLE, "abcd@gmail.com", "12345678901234567890");

        User user1 = User.of(Provider.GOOGLE, "a@gmail.com", "1234567890123456789");
        User user2 = User.of(Provider.GOOGLE, "ab@gmail.com", "123456789012345678");
        User user3 = User.of(Provider.GOOGLE, "abcd@gmail.com", "12345678901234567");

        TeamMember teamMember1 = TeamMember.of(user1, team,  NORMAL);
        TeamMember teamMember2 = TeamMember.of(user2, team,  NORMAL);
        TeamMember teamMember3 = TeamMember.of(user3, team,  MIDDLE_ADMIN);

        teamMembers.add((teamMember1));
        teamMembers.add((teamMember2));
        teamMembers.add((teamMember3));
    }

    @Test
    @DisplayName("관리자가 팀에서의 권한 설정을 위한 팀 멤버 리스트 조회 테스트")
    public void teamMemberService_findTeamMembers_Return_List_TeamMemberResponseDto_() {

        TeamMember teamMember = TeamMember.of(user, team, HIGHEST_ADMIN);
        teamMembers.add(teamMember);

        when(teamMemberRepository.findByTeam_IdAndUser_Id(any(Long.class), any(Long.class))).
                thenReturn(Optional.of(teamMember));
        when(teamMemberRepository.findByTeam_Id(any(Long.class))).thenReturn(teamMembers);

        List<TeamMemberResponseDto> teamMembersDto = teamMemberService.findTeamMembers(1L, 1L);

        assertThat(teamMembersDto).isNotNull();
        assertThat(teamMembersDto.size()).isEqualTo(teamMembers.size());
        assertThat(teamMembersDto.get(0).userNickname()).isEqualTo(teamMembers.get(0).getUserNickname());
        assertThat(teamMembersDto.get(1).userNickname()).isEqualTo(teamMembers.get(1).getUserNickname());
        assertThat(teamMembersDto.get(2).userNickname()).isEqualTo(teamMembers.get(2).getUserNickname());
        assertThat(teamMembersDto.get(3).teamGrant()).isEqualTo(teamMembers.get(3).getTeamGrant());

    }

    @Test
    @DisplayName("일반 멤버가 팀에서의 권한 설정을 위한 팀 멤버 리스트 조회 테스트")
    public void teamMemberService_findTeamMembers_Throw_IllegalStateException() {

        TeamMember teamMember = TeamMember.of(user, team, NORMAL);
        teamMembers.add(teamMember);

        when(teamMemberRepository.findByTeam_IdAndUser_Id(any(Long.class), any(Long.class))).
                thenReturn(Optional.of(teamMember));


    }


    @Test
    @DisplayName("팀 멤버 삭제 테스트")
    public void teamMemberService_deleteTeamMember_Return_Void() {

    }

    @Test
    @DisplayName("팀 멤버 권한 수정 테스트")
    public void teamMemberService_updateTeamGrant_Return_UpdateTeamGrantDto() {

    }

    @Test
    @DisplayName("팀의 최상위 계층은 오직 한명이다.")
    public void setTeamService_HIGHEST_ADMIN() {

    }

}