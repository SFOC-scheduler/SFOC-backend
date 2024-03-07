package com.project.sfoc.entity.schedule;

import com.project.sfoc.entity.teammember.TeamMember;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_member_id")
    private TeamMember teamMember;

    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = PeriodRepeatConverter.class)
    private PeriodRepeat periodRepeat;

    private Schedule(TeamMember teamMember, PeriodRepeat periodRepeat) {
        this.teamMember = teamMember;
        this.periodRepeat = periodRepeat;
    }

    public static Schedule of(TeamMember teamMember, PeriodRepeat periodRepeat) {
        return new Schedule(teamMember, periodRepeat);
    }

}
