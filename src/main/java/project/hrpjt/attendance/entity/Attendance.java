package project.hrpjt.attendance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import project.hrpjt.employee.entity.Employee;

import javax.persistence.*;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "UniqueEmpIdAndYear", columnNames = {"employee_id", "year"})
        }
)
@Getter
@NoArgsConstructor
public class Attendance {
    @Id @GeneratedValue
    @Column(name = "attendance_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private int year;
    private int tardy;  // 지각 (30분 이상)
    private int absenteeism;  // 무단 결근
    private int leaveEarly; // 조퇴 (1시간 이상)

    @Builder
    public Attendance(Employee employee, int year, int tardy, int absenteeism, int leaveEarly) {
        this.employee = employee;
        this.year = year;
        this.tardy = tardy;
        this.absenteeism = absenteeism;
        this.leaveEarly = leaveEarly;
    }
}
