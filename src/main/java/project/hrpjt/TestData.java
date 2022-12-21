package project.hrpjt;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final UserDetailsService userDetailsService;

    @PostConstruct
    @Transactional
    public void save() {
        OrganizationSaveDto company = OrganizationSaveDto.builder()
                .orgNm("company")
                .orgNo("000001")
                .startDate(LocalDate.of(2022, 01, 01))
                .build();

        organizationService.save(company);

        OrganizationSaveDto org = OrganizationSaveDto.builder()
                .orgNm("인사부")
                .orgNo("000010")
                .startDate(LocalDate.of(2022, 01, 01))
                .parentOrgNo("000001")
                .build();

        organizationService.save(org);

        OrganizationSaveDto org2 = OrganizationSaveDto.builder()
                .orgNm("총무부")
                .orgNo("000011")
                .startDate(LocalDate.of(2022, 01, 01))
                .parentOrgNo("000001")
                .build();

        organizationService.save(org2);

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
                .orgNo("000021")
                .startDate(LocalDate.of(2022, 01, 01))
                .parentOrgNo("000011")
                .build();

        organizationService.save(org4);

        OrganizationSaveDto org5 = OrganizationSaveDto.builder()
                .orgNm("총무 2팀")
                .orgNo("000022")
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
                .password("1234")
                .build();

        EmployeeSaveDto admin = EmployeeSaveDto.builder()
                .empNo("ADMIN")
                .birthDate(LocalDate.of(1970, 01, 01))
                .empNm("ADMIN")
                .gender("M")
                .role("ROLE_SYS_ADMIN")
                .hireDate(LocalDate.of(1990, 01, 01))
                .password("1234")
                .build();

        EmployeeSaveDto leader = EmployeeSaveDto.builder()
                .empNo("ORG_LEADER")
                .birthDate(LocalDate.of(1970, 01, 01))
                .empNm("ORG_LEADER")
                .gender("M")
                .role("ROLE_ORG_LEADER")
                .hireDate(LocalDate.of(1990, 01, 01))
                .password("1234")
                .build();

        EmployeeSaveDto employee = EmployeeSaveDto.builder()
                .empNo("EMPLOYEE")
                .birthDate(LocalDate.of(2000, 01, 01))
                .empNm("EMPLOYEE")
                .gender("M")
                .role("ROLE_EMPLOYEE")
                .hireDate(LocalDate.of(2022, 01, 01))
                .password("1234")
                .build();

        EmployeeSaveDto employee2 = EmployeeSaveDto.builder()
                .empNo("EMPLOYEE2")
                .birthDate(LocalDate.of(2000, 01, 01))
                .empNm("EMPLOYEE2")
                .gender("M")
                .role("ROLE_EMPLOYEE")
                .hireDate(LocalDate.of(2022, 01, 01))
                .password("1234")
                .build();

        EmployeeSaveDto leader2 = EmployeeSaveDto.builder()
                .empNo("LEADER2")
                .birthDate(LocalDate.of(2000, 01, 01))
                .empNm("LEADER2")
                .gender("M")
                .role("ROLE_ORG_LEADER")
                .hireDate(LocalDate.of(1998, 01, 01))
                .password("1234")
                .build();

        employeeService.save(admin);
        employeeService.save(ceo);
        employeeService.save(employee);
        employeeService.save(employee2);
        employeeService.save(leader);
        employeeService.save(leader2);

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
//                .endDate(LocalDate.of(1999,12,30))
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

        AppointmentSaveDto app6 = AppointmentSaveDto.builder()
                .type(AppointmentType.ORG)
                .status(AppointmentStatus.APPR)
                .orgNo("000001")
                .startDate(LocalDate.of(1980, 01, 01))
                .empNo("CEO")
                .build();

        AppointmentSaveDto app7 = AppointmentSaveDto.builder()
                .type(AppointmentType.ORG)
                .status(AppointmentStatus.APPR)
                .orgNo("000020")
                .startDate(LocalDate.of(1997, 01, 01))
                .empNo("ORG_LEADER")
                .build();

        AppointmentSaveDto app8 = AppointmentSaveDto.builder()
                .type(AppointmentType.ORG)
                .status(AppointmentStatus.APPR)
                .orgNo("000020")
                .startDate(LocalDate.of(2022, 01, 01))
                .empNo("EMPLOYEE")
                .build();

        AppointmentSaveDto app9 = AppointmentSaveDto.builder()
                .type(AppointmentType.ORG)
//                .status(AppointmentStatus.APPR)
                .orgNo("000022")
                .startDate(LocalDate.of(2022, 01, 01))
                .empNo("EMPLOYEE2")
                .build();

        AppointmentSaveDto app10 = AppointmentSaveDto.builder()
                .type(AppointmentType.ORG)
                .status(AppointmentStatus.APPR)
                .orgNo("000011")
                .startDate(LocalDate.of(1998, 01, 01))
                .empNo("LEADER2")
                .build();

        UserDetails userDetails = userDetailsService.loadUserByUsername("ADMIN");
        userDetails.getAuthorities();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        appointmentService.save(app1);
        appointmentService.save(app2);
        appointmentService.save(app3);
        appointmentService.save(app4);
        appointmentService.save(app5);
        appointmentService.save(app6);
        appointmentService.save(app7);
        appointmentService.save(app8);
        appointmentService.save(app9);
        appointmentService.save(app10);

        appointmentService.approve(17L, null);
        appointmentService.approve(20L, null);
        appointmentService.approve(21L, null);
        appointmentService.approve(22L, null);
        appointmentService.approve(24L, null);
    }
}
