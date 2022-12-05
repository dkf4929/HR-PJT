package project.hrpjt.employee.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import project.hrpjt.base.SubEntity;
import project.hrpjt.family.entity.Family;
import project.hrpjt.organization.entity.Organization;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Employee extends SubEntity implements UserDetails {
    @Id
    @GeneratedValue
    @Column(name = "employee_id")
    private Long id;

    @Column(unique = true, nullable = false, length = 10)
    private String employeeNo;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 15)
    private String role;

    @Column(nullable = false, length = 20)
    private String employeeName;

    @Column(nullable = false, length = 1)
    private String gender;
    
    @Column(nullable = false)
    private LocalDate birthDate;
    
    @Column(nullable = false)
    private LocalDate hireDate;

    private LocalDate retireDate;

    @Email
    private String kakaoMail;
    private String kakaoId;

    @OneToMany(mappedBy = "employee")
    private List<Family> families = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private Organization organization;

    public void addFamily(Family family) {
        families.add(family);
    }

    @Builder
    public Employee(String employeeNo, String password, String role, String employeeName, String gender, LocalDate birthDate, LocalDate hireDate, LocalDate retireDate, String kakaoMail, String kakaoId, Organization organization) {
        this.employeeNo = employeeNo;
        this.password = password;
        this.role = role;
        this.employeeName = employeeName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.hireDate = hireDate;
        this.retireDate = retireDate;
        this.kakaoMail = kakaoMail;
        this.kakaoId = kakaoId;
        this.organization = organization;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        SimpleGrantedAuthority auth = new SimpleGrantedAuthority(this.role);

        list.add(auth);
        return list;
    }

    @Override
    public String getUsername() {
        return this.employeeName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void updateEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateRole(String role) {
        this.role = role;
    }

    public void updateEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void updateHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public void updatRetireDate(LocalDate retireDate) {
        this.retireDate = retireDate;
    }

    public void updateOrganization(Organization organization) {
        this.organization = organization;
    }
}
