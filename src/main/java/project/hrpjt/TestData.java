package project.hrpjt;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.employee.dto.EmployeeSaveDto;
import project.hrpjt.employee.service.EmployeeService;
import project.hrpjt.organization.dto.OrganizationSaveDto;
import project.hrpjt.organization.entity.Organization;
import project.hrpjt.organization.service.OrganizationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
                .orgNo("000010")
                .startDate(LocalDate.now())
                .parentOrgId(1L)
                .build();

        Organization save2 = organizationService.save(org);

        OrganizationSaveDto org2 = OrganizationSaveDto.builder()
                .orgNm("총무부")
                .orgNo("000011")
                .startDate(LocalDate.now())
                .parentOrgId(1L)
                .build();

        Organization save3 = organizationService.save(org2);

        OrganizationSaveDto org3 = OrganizationSaveDto.builder()
                .orgNm("인사 1팀")
                .orgNo("000020")
                .startDate(LocalDate.now())
                .parentOrgId(2L)
                .build();

        organizationService.save(org3);

        OrganizationSaveDto o = OrganizationSaveDto.builder()
                .orgNm("인사 1-1팀")
                .orgNo("000030")
                .startDate(LocalDate.now())
                .parentOrgId(4L)
                .build();

        organizationService.save(o);

        OrganizationSaveDto org4 = OrganizationSaveDto.builder()
                .orgNm("총무 1팀")
                .orgNo("000031")
                .startDate(LocalDate.now())
                .parentOrgId(3L)
                .build();

        organizationService.save(org4);

        OrganizationSaveDto org5 = OrganizationSaveDto.builder()
                .orgNm("총무 2팀")
                .orgNo("000032")
                .startDate(LocalDate.now())
                .parentOrgId(3L)
                .build();

        organizationService.save(org5);

        EmployeeSaveDto ceo = EmployeeSaveDto.builder()
                .empNo("CEO")
                .birthDate(LocalDate.of(1960, 01, 01))
                .empNm("CEO")
                .gender("M")
                .role("ROLE_CEO")
                .hireDate(LocalDate.now())
                .organizationId(1L)
                .password("1234")
                .build();

        EmployeeSaveDto admin = EmployeeSaveDto.builder()
                .empNo("ADMIN")
                .birthDate(LocalDate.of(1970, 01, 01))
                .empNm("ADMIN")
                .gender("M")
                .role("ROLE_SYS_ADMIN")
                .hireDate(LocalDate.now())
                .organizationId(2L)
                .password("1234")
                .build();

        EmployeeSaveDto leader = EmployeeSaveDto.builder()
                .empNo("ORG_LEADER")
                .birthDate(LocalDate.of(1970, 01, 01))
                .empNm("ORG_LEADER")
                .gender("M")
                .role("ROLE_ORG_LEADER")
                .hireDate(LocalDate.now())
                .organizationId(4L)
                .password("1234")
                .build();

        EmployeeSaveDto user = EmployeeSaveDto.builder()
                .empNo("USER")
                .birthDate(LocalDate.of(2000, 01, 01))
                .empNm("USER")
                .gender("M")
                .role("ROLE_EMPLOYEE")
                .hireDate(LocalDate.now())
                .organizationId(4L)
                .password("1234")
                .build();

        employeeService.save(admin);
        employeeService.save(ceo);
        employeeService.save(user);
        employeeService.save(leader);
    }
}
