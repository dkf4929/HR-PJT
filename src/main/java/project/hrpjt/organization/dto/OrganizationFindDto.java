package project.hrpjt.organization.dto;

import lombok.*;
import project.hrpjt.organization.entity.Organization;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@ToString
public class OrganizationFindDto {
    private String orgNo;
    private String orgNm;
    private Set<OrganizationFindDto> child = new HashSet<>();

    @Builder
    public OrganizationFindDto(Organization organization, Set<Organization> childs) {
        this.orgNo = organization.getOrgNo();
        this.orgNm = organization.getOrgNm();
        this.child = childs.stream()
                .map(c -> OrganizationFindDto.builder()
                        .organization(c)
                        .childs(c.getChildren()).build())
                .collect(Collectors.toSet());
    }
}