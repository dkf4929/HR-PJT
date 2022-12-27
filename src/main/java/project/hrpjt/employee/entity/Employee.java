package project.hrpjt.employee.entity;
import javax.persistence.*;
import javax.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
@Getter
@NoArgsConstructor
@ToString(of = {"empNm"})
public class Employee extends SubEntity implements UserDetails {
    @Id
    @GeneratedValue
    @Column(name = "employee_id")
    private Long id;

    @Column(unique = true, nullable = false, length = 10)
    private String empNo;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 15)
    private String role;

    @Column(nullable = false, length = 20)
    private String empNm;

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

    @Email
    private String externalMail;

    @OneToMany(mappedBy = "employee")
    private List<Family> families = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private Organization organization;

    public void addFamily(Family family) {
        families.add(family);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Builder
    public Employee(String empNo, String password, String role, String empNm, String gender, LocalDate birthDate, LocalDate hireDate, LocalDate retireDate, String kakaoMail, String kakaoId, String externalMail) {
        this.empNo = empNo;
        this.password = password;
        this.role = role;
        this.empNm = empNm;
        this.gender = gender;
        this.birthDate = birthDate;
        this.hireDate = hireDate;
        this.retireDate = retireDate;
        this.kakaoMail = kakaoMail;
        this.kakaoId = kakaoId;
        this.externalMail = externalMail;
//        this.organization = organization;
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
        return this.empNm;
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

    public void updateempNo(String empNo) {
        this.empNo = empNo;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateRole(String role) {
        this.role = role;
    }

    public void updateEmpNm(String empNm) {
        this.empNm = empNm;
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
