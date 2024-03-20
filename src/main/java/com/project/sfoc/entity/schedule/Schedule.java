package com.project.sfoc.entity.schedule;

import com.project.sfoc.entity.teammember.TeamMember;
import jakarta.persistence.*;
import lombok.*;
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

    private String title;
    private String memo;

    @ManyToOne
    @JoinColumn(name = "team_member_id")
    private TeamMember teamMember;

    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = PeriodRepeatConverter.class)
    private PeriodRepeat periodRepeat;

    private Schedule(String title, String memo, TeamMember teamMember, PeriodRepeat periodRepeat) {
        this.title = title;
        this.memo = memo;
        this.teamMember = teamMember;
        this.periodRepeat = periodRepeat;
    }

    public static Schedule of(String title, String memo, TeamMember teamMember, PeriodRepeat periodRepeat) {
        return new Schedule(title, memo, teamMember, periodRepeat);
    }

}
