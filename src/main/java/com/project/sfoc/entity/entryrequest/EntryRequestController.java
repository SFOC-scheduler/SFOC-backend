package com.project.sfoc.entity.entryrequest;

import com.project.sfoc.entity.entryrequest.dto.RequestDeleteTeamRequestDto;
import com.project.sfoc.entity.entryrequest.dto.ResponseRequestEntryDto;
import com.project.sfoc.security.jwt.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/teams/{teamId}/request")
public class EntryRequestController {

    private final EntryRequestService entryRequestService;

    @GetMapping
    public ResponseEntity<List<ResponseRequestEntryDto>> getRequestEntry(@AuthenticationPrincipal UserInfo userInfo,
                                                                         @PathVariable(value = "teamId") Long teamId) {

        List<ResponseRequestEntryDto> requestEntries = entryRequestService.getRequestEntries(userInfo.id(), teamId);
        return ResponseEntity.ok(requestEntries);
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteRequest(@RequestBody RequestDeleteTeamRequestDto requestRefuseDto) {
        entryRequestService.applyOrRequest(requestRefuseDto);

        return ResponseEntity.ok().build();
    }

}
