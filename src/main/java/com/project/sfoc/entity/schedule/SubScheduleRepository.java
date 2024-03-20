package com.project.sfoc.entity.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SubScheduleRepository extends JpaRepository<SubSchedule, Long> {

    @Query("""
        select ss
        from SubSchedule ss
        join ss.schedule.teamMember.user u
        where ss.id = :subScheduleId and u.id = :userId
    """)
    Optional<SubSchedule> findByIdAndUserId(Long subScheduleId, Long userId);

    long countBySchedule_Id(Long scheduleId);

}
