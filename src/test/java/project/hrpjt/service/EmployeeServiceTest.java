package project.hrpjt.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.employee.dto.EmployeeSaveDto;
import project.hrpjt.employee.dto.EmployeeUpdateDto;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.employee.repository.EmployeeRepository;
import project.hrpjt.employee.service.EmployeeService;
import project.hrpjt.organization.repository.OrganizationRepository;
import project.hrpjt.organization.service.OrganizationService;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
public class EmployeeServiceTest {
    @Autowired EmployeeService employeeService;
    @Autowired OrganizationRepository organizationRepository;
    @Autowired UserDetailsService userDetailsService;
    @Autowired OrganizationService organizationService;
    @Autowired EmployeeRepository employeeRepository;

    @BeforeEach
    void each() throws Exception {
        UserDetails userDetails = userDetailsService.loadUserByUsername("ADMIN");
        userDetails.getAuthorities();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("시스템 권한으로 직원 생성")
    void adminAuthTest() throws Exception {
        EmployeeSaveDto dto = EmployeeSaveDto.builder()
                .empNo("userA")
                .empNm("userA")
                .gender("M")
                .role("role-empLOYEE")
                .password("1234")
                .hireDate(LocalDate.now())
                .birthDate(LocalDate.now())
                .build();

        employeeService.save(dto);

        Employee userA = employeeRepository.findByEmpNo("userA").get();

        assertThat(userA.getEmpNo()).isEqualTo("userA");
    }

    @Test
    @DisplayName("직원 정보 수정")
    void update() throws Exception {
        EmployeeUpdateDto dto = EmployeeUpdateDto.builder()
                .empNo("EMPLOYEE")
                .role("ROLE_ORG_LEADER")
                .build();

        employeeService.edit(dto);

        Employee employee = employeeRepository.findByEmpNo("EMPLOYEE").get();
        Assertions.assertThat(employee.getRole()).isEqualTo("ROLE_ORG_LEADER");
    }

}
