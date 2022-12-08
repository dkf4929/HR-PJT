package project.hrpjt.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.organization.entity.Organization;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class OrganizerFindDto {
    private String orgNo;
    private String orgNm;
    private List<OrganizerEmpDto> empInfo = new ArrayList<>();
    private List<OrganizerFindDto> child = new ArrayList<>();

    @Builder
    public OrganizerFindDto(Organization organization) {
        this.orgNo = organization.getOrgNo();
        this.orgNm = organization.getOrgNm();
        this.empInfo = organization.getEmployees().stream()
                .map((e) -> OrganizerEmpDto.builder().empNo(e.getEmpNo()).empNm(e.getEmpNm()).role(e.getRole()).build())
                .collect(Collectors.toList());
        this.child = organization.getChildren().stream()
                .map(o -> OrganizerFindDto.builder().organization(o).build())
                .collect(Collectors.toList());
    }
}
