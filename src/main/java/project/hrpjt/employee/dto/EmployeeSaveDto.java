package project.hrpjt.employee.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class EmployeeSaveDto {
    @NotEmpty
    private String employeeNo;

    @NotEmpty
    private String password;

    @NotEmpty
    private String role;

    @NotEmpty
    private String employeeName;

    @NotEmpty
    private String gender;

    @NotNull
    private LocalDate birthDate;

    @NotNull
    private LocalDate hireDate;

    private LocalDate retireDate;

    @Email
    private String kakaoMail;
    private String kakaoId;
}
