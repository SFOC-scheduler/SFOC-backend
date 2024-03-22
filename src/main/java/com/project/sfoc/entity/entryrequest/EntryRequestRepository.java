package com.project.sfoc.entity.entryrequest;

import org.springframework.data.jpa.repository.JpaRepository;


public interface EntryRequestRepository extends JpaRepository<EntryRequest, Long> {

    boolean existsByTeam_IdAndUser_Id(Long teamId, Long userId);
}
