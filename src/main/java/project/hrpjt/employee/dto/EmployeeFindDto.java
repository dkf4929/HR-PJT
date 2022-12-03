package project.hrpjt.employee.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.hrpjt.family.entity.Family;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeFindDto {
    private String employeeNo;
    private String role;
    private String employeeName;
    private String gender;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private LocalDate retireDate;
    private List<Family> families;
}
