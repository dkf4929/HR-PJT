package project.hrpjt.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.hrpjt.employee.entity.Employee;

import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OrganizationUpdateDto {
    @NotNull
    private Long updateOrgId;
    private String orgNo;
    private String orgNm;
    private Long parentId;
    private List<Long> addEmpIds;
    private List<Long> deleteEmpIds;
}
