package project.hrpjt.appointment.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import project.hrpjt.appointment.entity.enumeration.ApprovementStatus;
import project.hrpjt.appointment.entity.enumeration.AppointmentType;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.organization.entity.Organization;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "UniqueEndDateAndType", columnNames = {"appointmentType", "endDate", "employee_id", "startDate"})
        }
)
@ToString(of = {"appointmentType", "approvementStatus", "startDate", "endDate"})
public class Appointment {
    @Id
    @GeneratedValue
    @Column(name = "appointment_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private AppointmentType appointmentType;

    @Enumerated(EnumType.STRING)
    private ApprovementStatus approvementStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization transOrg;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    @ColumnDefault("29991231")
    private LocalDate endDate;

    @Builder
    public Appointment(AppointmentType appointmentType, ApprovementStatus approvementStatus, Employee employee, Organization transOrg, LocalDate startDate, LocalDate endDate) {
        if (approvementStatus == null) {
            this.approvementStatus = ApprovementStatus.LEADER_PENDING_APPR;
        } else {
            this.approvementStatus = approvementStatus;
        }
        this.appointmentType = appointmentType;
        this.employee = employee;
        this.transOrg = transOrg;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateApprovementStatus(ApprovementStatus approvementStatus) {
        this.approvementStatus = approvementStatus;
    }

    public void updateEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
