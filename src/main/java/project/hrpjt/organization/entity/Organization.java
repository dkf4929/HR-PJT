package project.hrpjt.organization.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import project.hrpjt.base.SubEntity;
import project.hrpjt.employee.entity.Employee;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
@ToString(of = {"orgNo", "orgNm"})
public class Organization extends SubEntity {
    @Id @GeneratedValue
    @Column(name = "org_id")
    private Long id;

    @Column(nullable = false, length = 6, unique = true)
    private String orgNo;

    @Column(nullable = false, length = 20, unique = true)
    private String orgNm;

    @Column(nullable = false)
    private LocalDate startDate;

    @ColumnDefault("29991231")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Organization parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Organization> children;

    @OneToMany(mappedBy = "organization")
    private List<Employee> employees = new ArrayList<>();

    @Builder
    public Organization(String orgNo, String orgNm, LocalDate startDate, LocalDate endDate, Set<Organization> children) {
        this.orgNo = orgNo;
        this.orgNm = orgNm;
        this.startDate = startDate;
        this.endDate = endDate;
        this.children = children;
    }

    public void addEmployee(Employee... employees) {
        for (Employee employee : employees) {
            this.employees.add(employee);
        }
    }

    public void updateParent(Organization parent) {
        this.parent = parent;
    }

    public void addChild(Organization child) {
        children = new HashSet<>();
        this.children.add(child);
    }

    public Organization getOrganization() {
        return this;
    }

    public void updateEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}