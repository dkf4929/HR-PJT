package project.hrpjt.employee.dto;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
    private Long organizationId;
    private String kakaoMail;
    private String kakaoId;
}
