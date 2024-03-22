package com.project.sfoc.entity.entryrequest;

import com.project.sfoc.entity.entryrequest.dto.RequestDeleteTeamRequestDto;
import com.project.sfoc.entity.entryrequest.dto.ResponseRequestEntryDto;
import com.project.sfoc.entity.team.Team;
import com.project.sfoc.entity.team.TeamRepository;
import com.project.sfoc.entity.teammember.TeamGrant;
import com.project.sfoc.entity.teammember.TeamMember;
import com.project.sfoc.entity.teammember.TeamMemberRepository;
import com.project.sfoc.entity.user.User;
import com.project.sfoc.entity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EntryRequestService {

    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final EntryRequestRepository entryRequestRepository;

    public List<ResponseRequestEntryDto> getRequestEntries(Long userId, Long teamId) {

        getTeamGrant(teamId, userId);

        List<User> users = userRepository.findRequestEntries(teamId);
        return users.stream().map(user -> ResponseRequestEntryDto.from(user, teamId)).toList();
    }


    public void applyOrRequest(RequestDeleteTeamRequestDto requestAcceptDto) {

        getTeamGrant(requestAcceptDto.teamId(), requestAcceptDto.userId());
        entryRequestRepository.deleteById(requestAcceptDto.entryRequestId());
        if(requestAcceptDto.apply()) {
            Team team = teamRepository.findById(requestAcceptDto.teamId()).
                    orElseThrow(IllegalArgumentException::new);
            User user = userRepository.findById(requestAcceptDto.userId()).
                    orElseThrow(IllegalArgumentException::new);
            TeamMember teamMember = TeamMember.of(user, team, TeamGrant.NORMAL);
            teamMemberRepository.save(teamMember);
            log.info("팀 참가 수락");
        } else {
            log.info("팀 참가 거절");
        }
    }

    private void getTeamGrant(Long teamId, Long userId) {
        TeamGrant teamGrant = teamMemberRepository.findByTeam_IdAndUser_Id(teamId, userId)
                .map(TeamMember::getTeamGrant)
                .orElseThrow(IllegalArgumentException::new);
        if(teamGrant == TeamGrant.NORMAL) {
            log.info("권한이 없습니다.");
            throw new IllegalStateException();
        }

    }
}
