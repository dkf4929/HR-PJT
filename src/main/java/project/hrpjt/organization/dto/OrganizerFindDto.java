package project.hrpjt.organization.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.organization.entity.Organization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
public class OrganizerFindDto {
    private String orgNo;
    private String orgNm;
    private Set<OrganizerFindDto> child = new HashSet<>();
    private List<OrganizerEmpDto> empList = new ArrayList<>();

    @JsonIgnore
    private Long parentId;

    @JsonIgnore
    private Long id;

    public OrganizerFindDto(Organization organization) {
        this.id = organization.getId();
        this.orgNo = organization.getOrgNo();
        this.orgNm = organization.getOrgNm();

        for (Employee employee : organization.getEmployees()) {
            OrganizerEmpDto build = OrganizerEmpDto.builder()
                    .empNo(employee.getEmpNo())
                    .empNm(employee.getEmpNm())
                    .role(employee.getRole())
                    .build();

            this.empList.add(build);
        }
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
