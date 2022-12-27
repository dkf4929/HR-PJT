package project.hrpjt.employee.dto;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.hrpjt.organization.entity.Organization;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeSaveDto {
    private String empNo;
    private String password;
    private String role;
    private String empNm;
    private String gender;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private String kakaoMail;
    private String kakaoId;
    private String externalMail;
}
