package project.hrpjt.family.entity;

import jakarta.persistence.*;
import project.hrpjt.base.SubEntity;
import project.hrpjt.employee.entity.Employee;

import java.time.LocalDate;

@Entity
public class Family extends SubEntity {
    @Id @GeneratedValue
    @Column(name = "family_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String famRel;

    @Column(nullable = false)
    private LocalDate brithDate;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public void addEmployee(Employee employee) {
        this.employee = employee;
    }
}
