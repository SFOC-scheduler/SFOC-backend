package com.project.sfoc.domain.admission_request.repository;

import com.project.sfoc.domain.admission_request.entity.AdmissionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmissionRequestRepository extends JpaRepository<AdmissionRequest, Long> {

}
