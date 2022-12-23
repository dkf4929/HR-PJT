package project.hrpjt.dayoffapply.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.hrpjt.appointment.entity.enumeration.ApprovementStatus;
import project.hrpjt.dayoffapply.entity.enumeration.DayOffType;
import project.hrpjt.employee.entity.Employee;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "uniqueDayOffApply", columnNames = {"employee_id", "startDate", "endDate"})
        }
)
public class DayOffApply {
    @Id
    @GeneratedValue
    @Column(name = "day_off_apply_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private DayOffType dayOffType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private double minusDays;

    @Enumerated(EnumType.STRING)
    private ApprovementStatus status;

    @Builder
    public DayOffApply(Employee employee, DayOffType dayOffType, LocalDate startDate, LocalDate endDate, ApprovementStatus status, double minusDays) {
        this.employee = employee;
        this.dayOffType = dayOffType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.minusDays = minusDays;

        if (status == null) {
            this.status = ApprovementStatus.LEADER_PENDING_APPR;
        }
    }

    public void updateStatus(ApprovementStatus status) {
        this.status = status;
    }
}
