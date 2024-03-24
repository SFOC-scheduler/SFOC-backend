package com.project.sfoc.entity.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubScheduleRepository extends JpaRepository<SubSchedule, Long> {

    List<SubSchedule> findAllBySchedule_Id(Long scheduleId);

}
