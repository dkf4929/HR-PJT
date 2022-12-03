package project.hrpjt.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.employee.dto.EmployeeSaveDto;
import project.hrpjt.employee.service.EmployeeService;

import java.time.LocalDate;

@Transactional
@SpringBootTest
public class EmployeeServiceTest {
    @Autowired
    EmployeeService employeeService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @Commit
    void save() {
        EmployeeSaveDto dto = EmployeeSaveDto.builder()
                .employeeNo("ADMIN")
                .birthDate(LocalDate.of(1970, 01, 01))
                .employeeName("ADMIN")
                .gender("M")
                .role("ROLE_ADMIN")
                .kakaoMail("dkf4929@nate.com")
                .hireDate(LocalDate.now())
                .password(passwordEncoder.encode("1234"))
                .build();

        employeeService.save(dto);
    }
}
