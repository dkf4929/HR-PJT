package project.hrpjt.dayoff.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.hrpjt.base.SubEntity;
import project.hrpjt.employee.entity.Employee;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Builder
public class DayOff extends SubEntity {
    @Id @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "employeeId")
    private Employee employee;

    private int annualLeave;
    private int specialLeave;
    private int extendedLeave;

    public DayOff(Employee employee, int annualLeave, int specialLeave, int extendedLeave) {
        this.employee = employee;
        this.annualLeave = annualLeave;
        this.specialLeave = specialLeave;
        this.extendedLeave = extendedLeave;
    }
}
