package project.hrpjt.organization.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import project.hrpjt.organization.entity.Organization;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class OrganizationFindDto {
    private String orgNo;
    private String orgNm;
    private Set<OrganizationFindDto> child = new HashSet<>();

    @Builder
    public OrganizationFindDto(Organization organization, Set<Organization> childs) {
        this.orgNo = organization.getOrgNo();
        this.orgNm = organization.getOrgNm();
        this.child = childs.stream().map(o -> OrganizationFindDto.builder()
                .organization(o)
                .childs(childs).build())
                .collect(Collectors.toSet());

//        this.child = childs.stream().map(o -> OrganizationFindDto.builder().organization(o).childs(o.getChildren()).build()).collect(Collectors.toSet());

    }
}