package com.project.sfoc.entity.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query(value = """
        select distinct s
        from Schedule s
        join fetch s.subSchedules ss
        where s.teamMember.team.id = :teamId
    """)
    List<Schedule> findAllByTeamMember_Team_Id(Long teamId);
    @Query(value = """
        select distinct s
        from Schedule s
        join fetch s.subSchedules ss
        where s.teamMember.user.id = :userId
    """)
    List<Schedule> findAllByTeamMember_User_Id(Long userId);
    @Query(value = """
        select s
        from Schedule s
        join fetch s.subSchedules ss
        where s.teamMember.team.id in (select t.id
                                       from TeamMember m2
                                       join m2.team t join m2.user u
                                       where u.id = :userId)
    """)
    List<Schedule> findAllByUser_Id(Long userId);
}
