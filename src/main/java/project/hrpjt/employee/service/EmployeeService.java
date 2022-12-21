package project.hrpjt.employee.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.employee.dto.EmployeeOrgDto;
import project.hrpjt.employee.dto.EmployeeUpdateDto;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.exception.NoAuthorityException;
import project.hrpjt.exception.NoSuchEmployeeException;
import project.hrpjt.employee.dto.EmployeeFindDto;
import project.hrpjt.employee.dto.EmployeeSaveDto;
import project.hrpjt.employee.repository.EmployeeRepository;
import project.hrpjt.organization.entity.Organization;
import project.hrpjt.organization.repository.OrganizationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder encoder;
    private final OrganizationRepository organizationRepository;

    public Employee save(EmployeeSaveDto param) {
        Employee employee = dtoToEntity(param);

        return employeeRepository.save(employee);
    }

    public EmployeeFindDto findEmployee(Long employeeId) {
        Employee employee = employeeRepository.findByIdFetch(employeeId).orElseThrow(() -> {
            throw new NoSuchEmployeeException("존재하지 않는 회원입니다.");
        });

        return entityToDto(employee);
    }

    public Page<EmployeeFindDto> findAll(Pageable pageable) {
        List<Employee> employees = employeeRepository.findAllFetch();

        List<EmployeeFindDto> collect = employees.stream()
                .map(m -> entityToDto(m))
                .collect(Collectors.toList());

        return new PageImpl<>(collect, pageable, collect.size());
    }

    public EmployeeFindDto edit(EmployeeUpdateDto param) {
        Employee loginEmp = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String role = loginEmp.getRole(); // 로그인한 직원의 권한 정보

        Employee employee = updateEmployeeInfo(role, param, loginEmp.getOrganization());

        return EmployeeFindDto.builder()
                .organization(EmployeeOrgDto.builder()
                        .organization(employee.getOrganization())
                        .build())
                .retireDate(employee.getRetireDate())
                .hireDate(employee.getHireDate())
                .empNo(employee.getEmpNo())
                .gender(employee.getGender())
                .empNm(employee.getEmpNm())
                .birthDate(employee.getBirthDate())
                .role(employee.getRole())
                .build();
    }

    private Employee updateEmployeeInfo(String role, EmployeeUpdateDto param, Organization org) {
        Employee employee = employeeRepository.findByEmpNo(param.getEmpNo()).orElseThrow(() -> {
            throw new NoSuchEmployeeException("존재하지 않는 사원입니다.");
        });

        if (role.equals("SYS_ADMIN") && param.getRole() != null && param.getRole().equals("ORG_LEADER")) {
            employee.updateRole(param.getRole());
        } else if (role.equals("CEO") && param.getRole() != null && (param.getRole().equals("SYS_ADMIN") || param.getRole().equals("ORG_LEADER"))) {
            employee.updateRole(param.getRole());
        }

        if (param.getUpdateEmpNo() != null && role.equals("ROLE_SYS_ADMIN")) {
            employee.updateempNo(param.getUpdateEmpNo());
        }
        if (param.getEmpNm() != null && role.equals("ROLE_SYS_ADMIN")) {
            employee.updateEmpNm(param.getEmpNm());
        }
        if (param.getPassword() != null) {
            employee.updatePassword(encoder.encode(param.getPassword()));
        }

        return employee;
    }

    private EmployeeFindDto entityToDto(Employee employee) {
        return EmployeeFindDto.builder()
                        .role(employee.getRole())
                        .empNm(employee.getEmpNm())
                        .birthDate(employee.getBirthDate())
                        .gender(employee.getGender())
//                        .families(employee.getFamilies())
                        .organization(EmployeeOrgDto.builder()
                                .organization(employee.getOrganization())
                                .build())
                        .empNo(employee.getEmpNo())
                        .hireDate(employee.getHireDate())
                        .retireDate(employee.getRetireDate())
                        .build();
    }

    private Employee dtoToEntity(EmployeeSaveDto param) {
        return Employee.builder()
                .empNo(param.getEmpNo())
                .password(encoder.encode(param.getPassword()))
                .empNm(param.getEmpNm())
                .kakaoMail(param.getKakaoMail())
                .hireDate(LocalDate.now())
                .kakaoId(param.getKakaoId())
                .role(param.getRole())
                .birthDate(param.getBirthDate())
                .gender(param.getGender())
                .build();
    }

}
