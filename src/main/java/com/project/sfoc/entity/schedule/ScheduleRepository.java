package com.project.sfoc.entity.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findAllByTeamMember_Id(Long teamMemberId);

    @Query(value = """
        select s
        from Schedule s
        join s.teamMember.user u
        where u.id = :userId
    """)
    List<Schedule> findAllByUser_Id(Long userId);
}
