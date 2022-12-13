package project.hrpjt.organization.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import project.hrpjt.organization.entity.Organization;
import java.util.*;

@NoArgsConstructor
@Getter
@ToString(of = {"orgNo", "orgNm", "parentId", "child"})
//@EqualsAndHashCode(of = {"orgNo", "orgNm"})
public class OrganizationFindDto {
    private String orgNo;
    private String orgNm;
    private Set<OrganizationFindDto> child = new HashSet<>();

    @JsonIgnore
    private Long parentId;

    @JsonIgnore
    private Long id;

    public OrganizationFindDto(Organization organization) {
        this.id = organization.getId();
        this.orgNo = organization.getOrgNo();
        this.orgNm = organization.getOrgNm();
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}