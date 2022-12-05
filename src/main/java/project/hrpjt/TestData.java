package project.hrpjt;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.employee.dto.EmployeeSaveDto;
import project.hrpjt.employee.service.EmployeeService;
import project.hrpjt.organization.dto.OrganizationSaveDto;
import project.hrpjt.organization.entity.Organization;
import project.hrpjt.organization.service.OrganizationService;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class TestData {
    private final EmployeeService employeeService;
    private final OrganizationService organizationService;

    @PostConstruct
    @Transactional
    public void save() {
        OrganizationSaveDto company = OrganizationSaveDto.builder()
                .orgNm("company")
                .orgNo("000001")
                .startDate(LocalDate.now())
                .build();

        Organization save1 = organizationService.save(company);

        OrganizationSaveDto org = OrganizationSaveDto.builder()
                .orgNm("인사부")
                .orgNo("000002")
                .startDate(LocalDate.now())
                .parentOrgId(1L)
                .build();

        Organization save2 = organizationService.save(org);

        EmployeeSaveDto admin = EmployeeSaveDto.builder()
                .employeeNo("ADMIN")
                .birthDate(LocalDate.of(1970, 01, 01))
                .employeeName("ADMIN")
                .gender("M")
                .role("ROLE_SYS_ADMIN")
                .hireDate(LocalDate.now())
                .organization(save2)
                .password("1234")
                .build();

        EmployeeSaveDto leader = EmployeeSaveDto.builder()
                .employeeNo("ORG_LEADER")
                .birthDate(LocalDate.of(1970, 01, 01))
                .employeeName("ORG_LEADER")
                .gender("M")
                .role("ROLE_ORG_LEADER")
                .hireDate(LocalDate.now())
                .organization(save2)
                .password("1234")
                .build();

        EmployeeSaveDto user = EmployeeSaveDto.builder()
                .employeeNo("USER")
                .birthDate(LocalDate.of(2000, 01, 01))
                .employeeName("USER")
                .gender("M")
                .role("ROLE_EMPLOYEE")
                .hireDate(LocalDate.now())
                .organization(save2)
                .password("1234")
                .build();

        employeeService.save(admin);
        employeeService.save(user);
        employeeService.save(leader);
    }
}
