package project.hrpjt.dayoff.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.hrpjt.attendance.entity.Attendance;
import project.hrpjt.base.SubEntity;
import project.hrpjt.employee.entity.Employee;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "uniqueDayOff", columnNames = {"employee_id", "year"})
        }
)
public class DayOff extends SubEntity {
    @Id @GeneratedValue
    @Column(name = "dayoff_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private int year;
    private double annualDayOff;
    private double specialDayOff;

    @Builder
    public DayOff(Employee employee, Attendance attendance) {
        this.employee = employee;

        createLeave(employee, attendance); // 연차 생성
    }

    private void createLeave(Employee employee, Attendance attendance) {
        int workYear = attendance.getYear() - employee.getHireDate().getYear();
        this.year = attendance.getYear() + 1; // 이전년도 근태 기준으로 연차 생성.

        if (workYear >= 2 && workYear < 4) { // 2,3년 차는 연차 16일
            annualDayOff = 16;
        } else if (workYear >= 4 && workYear < 6) { // 4,5년차 연차 18일
            annualDayOff = 18;
        } else if (workYear > 6) { // 6년차 이상 연차 20일
            annualDayOff = 20;

            if (workYear == 10) { // 근속 10년 특별 휴가 5일
                specialDayOff = 5;
            } else if (workYear == 10) { // 근속 20년 특별 휴가 10일
                specialDayOff = 10;
            } else if (workYear == 20) {
                specialDayOff = 15;
            }
        } else {
            annualDayOff = 15;
        }

        double minusDays = 0;

        if (attendance.getTardy() > 0) {
            minusDays = attendance.getTardy() / 3; // 지각 3회 -> 연차 -1일
        }

        if (attendance.getAbsenteeism() > 0) {
            minusDays += attendance.getAbsenteeism() * 2; // 무단결근 1회 -> 연차 -2일
        }

        if (attendance.getLeaveEarly() > 0) {
            minusDays += attendance.getLeaveEarly() / 3; // 조퇴 3번 -> 연차 -1일
        }

        annualDayOff = annualDayOff - minusDays;
    }

    public void updateAnnualDayOff(double annualDayOff) {
        this.annualDayOff = annualDayOff;
    }

    public void updateSpecialDayOff(double specialDayOff) {
        this.specialDayOff = specialDayOff;
    }
}
