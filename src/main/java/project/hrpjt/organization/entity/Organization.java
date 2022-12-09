package project.hrpjt.organization.entity;

import lombok.*;
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
@ToString(of = {"orgNm"})
@NoArgsConstructor
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

    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Organization parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private Set<Organization> children = new HashSet<>();

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

    public void addChild(Organization... childs) {
        for (Organization child : childs) {
            this.children.add(child);
        }
    }

    public Organization getOrganization() {
        return this;
    }
}