package com.project.sfoc.domain.entryrequest.repository;

import com.project.sfoc.domain.entryrequest.entity.EntryRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryRequestRepository extends JpaRepository<EntryRequest, Long> {

}
