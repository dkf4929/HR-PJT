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
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.exception.NoSuchEmployeeException;
import project.hrpjt.employee.dto.EmployeeFindDto;
import project.hrpjt.employee.dto.EmployeeSaveDto;
import project.hrpjt.employee.repository.EmployeeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder encoder;

    public Employee save(EmployeeSaveDto param) {
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

        if (deleteEmployee.getRole().equals("ROLE_ADMIN")) {
            throw new IllegalStateException("관리자는 삭제할 수 없습니다.");
        }

        employeeRepository.deleteById(employeeId);
    }

    private EmployeeFindDto entityToDto(Employee employee) {
        return EmployeeFindDto.builder()
                        .role(employee.getRole())
                        .employeeName(employee.getEmployeeName())
                        .birthDate(employee.getBirthDate())
                        .gender(employee.getGender())
                        .families(employee.getFamilies())
                        .employeeNo(employee.getEmployeeNo())
                        .hireDate(employee.getHireDate())
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
                .birthDate(param.getBirthDate())
                .gender(param.getGender())
                .build();
    }

}
