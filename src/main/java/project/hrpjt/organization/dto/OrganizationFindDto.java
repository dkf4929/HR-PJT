package project.hrpjt.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.hrpjt.organization.entity.Organization;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class OrganizationFindDto {
    private String orgNo;
    private String orgNm;
    private List<OrganizationFindDto> child;

    @Builder
    public OrganizationFindDto(Organization organization) {
        this.orgNo = organization.getOrgNo();
        this.orgNm = organization.getOrgNm();
        this.child = organization.getChildren().stream().map(OrganizationFindDto::new).collect(Collectors.toList());
    }
}