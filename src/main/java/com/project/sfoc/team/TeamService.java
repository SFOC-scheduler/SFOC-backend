package com.project.sfoc.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;


    public void createTeam(TeamRequestDto teamRequestDto) {
        final Team team = teamRequestDto.toEntity(createInvitationCode());
        teamRepository.save(team);
    }


    private String createInvitationCode() {
        String randomUUID = UUID.randomUUID().toString();
        String[] split = randomUUID.split("-");
        return split[0];
    }

}
