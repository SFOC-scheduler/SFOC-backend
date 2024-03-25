package com.project.sfoc.entity.entryrequest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface EntryRequestRepository extends JpaRepository<EntryRequest, Long> {

    boolean existsByTeam_IdAndUser_Id(Long teamId, Long userId);

    Optional<EntryRequest> findByTeam_IdAndUser_Id(Long teamId, Long usreId);
}
