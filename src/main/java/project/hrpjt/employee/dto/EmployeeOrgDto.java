package project.hrpjt.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.hrpjt.organization.entity.Organization;

@NoArgsConstructor
@Getter
public class EmployeeOrgDto {
    private String orgNo;
    private String orgNm;

    @Builder
    public EmployeeOrgDto(Organization organization) {
        if (organization != null) {
            this.orgNo = organization.getOrgNo();
            this.orgNm = organization.getOrgNm();
        }
    }
}
