package com.project.sfoc.entity.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findAllByTeamMember_Team_Id(Long teamId);
    List<Schedule> findAllByTeamMember_User_Id(Long userId);
    @Query(value = """
        select s
        from Schedule s
        join s.teamMember m1
        where m1.team.id in (select t.id
                             from TeamMember m2
                             join m2.team t join m2.user u
                             where u.id = :userId)
    """)
    List<Schedule> findAllByUser_Id(Long userId);
}
