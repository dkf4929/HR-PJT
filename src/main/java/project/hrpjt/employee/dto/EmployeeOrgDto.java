package project.hrpjt.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class EmployeeOrgDto {
    private String orgNo;
    private String orgNm;
}
