package project.hrpjt;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.appointment.dto.AppointmentFindDto;
import project.hrpjt.appointment.dto.AppointmentSaveDto;
import project.hrpjt.appointment.entity.enumeration.AppointmentStatus;
import project.hrpjt.appointment.entity.enumeration.AppointmentType;
import project.hrpjt.appointment.service.AppointmentService;
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
    private final AppointmentService appointmentService;

    @PostConstruct
    @Transactional
    public void save() {
        OrganizationSaveDto company = OrganizationSaveDto.builder()
                .orgNm("company")
                .orgNo("000001")
                .startDate(LocalDate.of(2022, 01, 01))
                .build();

        Organization save1 = organizationService.save(company);

        OrganizationSaveDto org = OrganizationSaveDto.builder()
                .orgNm("인사부")
                .orgNo("000010")
                .startDate(LocalDate.of(2022, 01, 01))
                .parentOrgNo("000001")
                .build();

        Organization save2 = organizationService.save(org);

        OrganizationSaveDto org2 = OrganizationSaveDto.builder()
                .orgNm("총무부")
                .orgNo("000011")
                .startDate(LocalDate.of(2022, 01, 01))
                .parentOrgNo("000001")
                .build();

        Organization save3 = organizationService.save(org2);

        OrganizationSaveDto org3 = OrganizationSaveDto.builder()
                .orgNm("인사 1팀")
                .orgNo("000020")
                .startDate(LocalDate.of(2022, 01, 01))
                .parentOrgNo("000010")
                .build();

        organizationService.save(org3);

        OrganizationSaveDto o = OrganizationSaveDto.builder()
                .orgNm("인사 1-1팀")
                .orgNo("000030")
                .startDate(LocalDate.of(2022, 01, 01))
                .parentOrgNo("000020")
                .build();

        organizationService.save(o);

        OrganizationSaveDto org4 = OrganizationSaveDto.builder()
                .orgNm("총무 1팀")
                .orgNo("000031")
                .startDate(LocalDate.of(2022, 01, 01))
                .parentOrgNo("000011")
                .build();

        organizationService.save(org4);

        OrganizationSaveDto org5 = OrganizationSaveDto.builder()
                .orgNm("총무 2팀")
                .orgNo("000032")
                .startDate(LocalDate.of(2022, 01, 01))
                .parentOrgNo("000011")
                .build();

        organizationService.save(org5);

        OrganizationSaveDto org6 = OrganizationSaveDto.builder()
                .orgNm("발령 대기")
                .orgNo("999999")
                .startDate(LocalDate.of(2022, 01, 01))
                .build();

        organizationService.save(org6);

        EmployeeSaveDto ceo = EmployeeSaveDto.builder()
                .empNo("CEO")
                .birthDate(LocalDate.of(1960, 01, 01))
                .empNm("CEO")
                .gender("M")
                .role("ROLE_CEO")
                .hireDate(LocalDate.of(1980, 01, 01))
                .orgNo("000001")
                .password("1234")
                .build();

        EmployeeSaveDto admin = EmployeeSaveDto.builder()
                .empNo("ADMIN")
                .birthDate(LocalDate.of(1970, 01, 01))
                .empNm("ADMIN")
                .gender("M")
                .role("ROLE_SYS_ADMIN")
                .hireDate(LocalDate.of(1990, 01, 01))
                .orgNo("000010")
                .password("1234")
                .build();

        EmployeeSaveDto leader = EmployeeSaveDto.builder()
                .empNo("ORG_LEADER")
                .birthDate(LocalDate.of(1970, 01, 01))
                .empNm("ORG_LEADER")
                .gender("M")
                .role("ROLE_ORG_LEADER")
                .hireDate(LocalDate.of(1990, 01, 01))
                .orgNo("000010")
                .password("1234")
                .build();

        EmployeeSaveDto user = EmployeeSaveDto.builder()
                .empNo("EMPLOYEE")
                .birthDate(LocalDate.of(2000, 01, 01))
                .empNm("EMPLOYEE")
                .gender("M")
                .role("ROLE_EMPLOYEE")
                .hireDate(LocalDate.of(2022, 01, 01))
                .orgNo("000020")
                .password("1234")
                .build();

        employeeService.save(admin);
        employeeService.save(ceo);
        employeeService.save(user);
        employeeService.save(leader);

        AppointmentSaveDto app1 = AppointmentSaveDto.builder()
                .type(AppointmentType.ORG)
                .orgNo("000030")
                .status(AppointmentStatus.APPR)
                .startDate(LocalDate.of(1990, 01, 01))
                .empNo("ADMIN")
                .endDate(LocalDate.of(1994,12,30))
                .build();

        AppointmentSaveDto app2 = AppointmentSaveDto.builder()
                .type(AppointmentType.ORG)
                .status(AppointmentStatus.APPR)
                .orgNo("000020")
                .startDate(LocalDate.of(1995, 01, 01))
                .endDate(LocalDate.of(1999,12,30))
                .empNo("ADMIN")
                .build();

        AppointmentSaveDto app3 = AppointmentSaveDto.builder()
                .type(AppointmentType.ORG)
                .status(AppointmentStatus.APPR)
                .orgNo("000010")
                .startDate(LocalDate.of(2000, 01, 01))
                .empNo("ADMIN")
                .build();

        AppointmentSaveDto app4 = AppointmentSaveDto.builder()
                .type(AppointmentType.DISEASE_LEAVE)
                .status(AppointmentStatus.APPR)
                .startDate(LocalDate.of(2010, 03, 01))
                .endDate(LocalDate.of(2010, 03, 04))
                .empNo("ADMIN")
                .build();

        AppointmentSaveDto app5 = AppointmentSaveDto.builder()
                .type(AppointmentType.RETURN)
                .status(AppointmentStatus.APPR)
                .startDate(LocalDate.of(2010, 03, 05))
                .empNo("ADMIN")
                .build();

        appointmentService.save(app1);
        appointmentService.save(app2);
        appointmentService.save(app3);
        appointmentService.save(app4);
        appointmentService.save(app5);
    }
}
