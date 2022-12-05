package project.hrpjt.organization.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import project.hrpjt.employee.entity.Employee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Organization {
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

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Organization> children = new ArrayList<>();

    @Builder
    public Organization(String orgNo, String orgNm, LocalDate startDate, LocalDate endDate) {
        this.orgNo = orgNo;
        this.orgNm = orgNm;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateParent(Organization parent) {
        this.parent = parent;
    }

    public void addChild(Organization... childs) {
        for (Organization child : childs) {
            this.children.add(child);
        }
    }
}
