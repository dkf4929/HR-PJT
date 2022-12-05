package project.hrpjt.employee.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import project.hrpjt.family.entity.Family;
import project.hrpjt.organization.dto.OrganizationFindDto;
import project.hrpjt.organization.entity.Organization;

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
    private EmployeeOrgDto organization;
    private List<Family> families;

}
