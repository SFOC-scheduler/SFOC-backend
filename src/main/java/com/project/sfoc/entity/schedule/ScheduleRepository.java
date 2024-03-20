package com.project.sfoc.entity.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query(value = """
        select s
        from Schedule s
        where s.id = (select ss.schedule.id
                      from SubSchedule ss
                      where ss.id = :subScheduleId)
        """)
    Optional<Schedule> findBySubscheduleId(Long subScheduleId);

}
