package project.hrpjt.organization.dto;

import lombok.*;
import project.hrpjt.organization.entity.Organization;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@ToString(of = {"orgNo", "orgNm"})
public class OrganizationFindDto {
    private String orgNo;
    private String orgNm;
    private List<OrganizationFindDto> child = new ArrayList<>();


    @Builder
    public OrganizationFindDto(Organization organization, Set<Organization> childs) {
        this.orgNo = organization.getOrgNo();
        this.orgNm = organization.getOrgNm();
        for (Organization child : childs) {
            this.child.add(OrganizationFindDto.builder().childs(child.getChildren()).organization(child).build());
        }
    }
}