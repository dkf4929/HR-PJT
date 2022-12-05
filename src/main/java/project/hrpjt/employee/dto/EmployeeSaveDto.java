package project.hrpjt.employee.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import project.hrpjt.organization.entity.Organization;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class EmployeeSaveDto {
    private String employeeNo;
    private String password;
    private String role;
    private String employeeName;
    private String gender;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private LocalDate retireDate;
    private Organization organization;
    private String kakaoMail;
    private String kakaoId;
}
