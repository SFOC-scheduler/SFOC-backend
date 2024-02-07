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


    public String createInvitationCode() {
        String code = null;
        while(code == null || isDuplicateUuidCode(code)) {
            String randomUUID = UUID.randomUUID().toString();
            String[] split = randomUUID.split("-");
            code = split[0];
        }

        return code;
    }

    private boolean isDuplicateUuidCode(String code) {
        return teamRepository.existsByInvitationCode(code);
    }

}
