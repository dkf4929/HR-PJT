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
//        Organization organization = organizationRepository.findById(param.getOrganizationId()).get();
        Employee employee = dtoToEntity(param);

        return employeeRepository.save(employee);
    }

    public EmployeeFindDto findEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> {
            throw new NoSuchEmployeeException("존재하지 않는 회원입니다.");
        });

        return entityToDto(employee);
    }

    public Page<EmployeeFindDto> findAll(Pageable pageable) {
        List<Employee> employees = employeeRepository.findAll();

        List<EmployeeFindDto> collect = employees.stream()
                .map(m -> entityToDto(m))
                .collect(Collectors.toList());

        return new PageImpl<>(collect);
    }

    public void delete(Long employeeId) {
        Employee loginEmployee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 인증 객체에서 로그인된 회원을 가져온다.

        Employee deleteEmployee = employeeRepository.findById(employeeId).orElseThrow(() -> {
            throw new UsernameNotFoundException("존재하지 않는 회원입니다.");
        });

        if (deleteEmployee.getRole().equals("ROLE_SYS_ADMIN")) {
            throw new IllegalStateException("관리자는 삭제할 수 없습니다."); // 관리자는 해당 기능에서 삭제 불가
        }

        employeeRepository.deleteById(employeeId);
    }

    public EmployeeFindDto edit(EmployeeUpdateDto param) {
        Employee loginEmp = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String role = loginEmp.getRole(); // 로그인한 직원의 권한 정보

        updateEmployeeInfo(role, param, loginEmp.getOrganization());

        return EmployeeFindDto.builder()
                .organization(EmployeeOrgDto.builder()
                        .orgNo(loginEmp.getOrganization().getOrgNo())
                        .orgNm(loginEmp.getOrganization().getOrgNm()).build())
                .retireDate(loginEmp.getRetireDate())
                .hireDate(loginEmp.getHireDate())
                .employeeNo(loginEmp.getEmployeeNo())
                .gender(loginEmp.getGender())
                .employeeName(loginEmp.getEmployeeName())
                .birthDate(loginEmp.getBirthDate())
                .role(loginEmp.getRole())
                .build();
    }

//     1. 패스워드를 제외한 항목은 유저 권한을 가진 직원이 수정 불가능함.
//     2. 관리자 권한을 가진 직원이 해당 직원을 같은 권한으로 설정할 수 없음.
//     3. 변경 가능한 권한 항목 -> sys_admin(org_leader 권한 부여 가능), ceo(sys_admin 권한 부여 가능)
//     4. 개인 정보는 해당 사원의 부서장 또는 시스템 관리자가 변경 가능. 사번, 성명, 조직은 시스템 관리자만 변경 가능.
    private void updateEmployeeInfo(String role, EmployeeUpdateDto param, Organization org) {
        Employee employee = employeeRepository.findById(param.getEmployeeId()).orElseThrow(() -> {
            throw new NoSuchEmployeeException("존재하지 않는 사원입니다.");
        });

        if (role.equals("SYS_ADMIN") && param.getRole() != null && param.getRole().equals("ORG_LEADER")) {
            employee.updateRole(param.getRole());
        } else if (role.equals("CEO") && param.getRole() != null && (param.getRole().equals("SYS_ADMIN") || param.getRole().equals("ORG_LEADER"))) {
            employee.updateRole(param.getRole());
        }

        if (param.getEmployeeNo() != null && role.equals("ROLE_SYS_ADMIN")) {
            employee.updateEmployeeNo(param.getEmployeeNo());
        }
        if (param.getEmployeeName() != null && role.equals("ROLE_SYS_ADMIN")) {
            employee.updateEmployeeName(param.getEmployeeName());
        }
        if (param.getOrganizationId() != null && role.equals("ROLE_SYS_ADMIN")) {
            Organization organization = organizationRepository.findById(param.getOrganizationId()).get();
            employee.updateOrganization(organization);
        }
        if (param.getRetireDate() != null && (role.equals("ROLE_ORG_LEADER") && org.getId() == employee.getOrganization().getId())) {
            employee.updatRetireDate(param.getRetireDate());
        }
        if (param.getPassword() != null) {
            employee.updatePassword(encoder.encode(param.getPassword()));
        }
    }

    private EmployeeFindDto entityToDto(Employee employee) {
        return EmployeeFindDto.builder()
                        .role(employee.getRole())
                        .employeeName(employee.getEmployeeName())
                        .birthDate(employee.getBirthDate())
                        .gender(employee.getGender())
//                        .families(employee.getFamilies())
                        .employeeNo(employee.getEmployeeNo())
                        .hireDate(employee.getHireDate())
//                        .organization(employee.getOrganization())
                        .retireDate(employee.getRetireDate())
                        .build();
    }

    private Employee dtoToEntity(EmployeeSaveDto param) {
        return Employee.builder()
                .employeeNo(param.getEmployeeNo())
                .password(encoder.encode(param.getPassword()))
                .employeeName(param.getEmployeeName())
                .kakaoMail(param.getKakaoMail())
                .hireDate(LocalDate.now())
                .kakaoId(param.getKakaoId())
                .role(param.getRole())
                .retireDate(param.getRetireDate())
                .organization(organizationRepository.findById(param.getOrganizationId()).get())
                .birthDate(param.getBirthDate())
                .gender(param.getGender())
                .build();
    }

}
