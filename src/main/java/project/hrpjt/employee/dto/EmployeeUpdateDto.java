package project.hrpjt.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.hrpjt.organization.entity.Organization;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class EmployeeUpdateDto {
    private String empNo;
    private String updateEmpNo;
    private String password;
    private String role;
    private String empNm;
}
