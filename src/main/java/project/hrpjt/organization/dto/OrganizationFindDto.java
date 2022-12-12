package project.hrpjt.organization.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import project.hrpjt.organization.entity.Organization;
import java.util.*;

@NoArgsConstructor
@Getter
@ToString(of = {"orgNo", "orgNm"})
@EqualsAndHashCode(of = {"orgNo", "orgNm"})
public class OrganizationFindDto {
    private String orgNo;
    private String orgNm;
    private Set<OrganizationFindDto> child = new HashSet<>();

    @JsonIgnore
    private static OrganizationFindDto parent;

    @Builder
    public OrganizationFindDto(Organization organization, Set<Organization> childs, OrganizationFindDto parent) {
        this.orgNo = organization.getOrgNo();
        this.orgNm = organization.getOrgNm();

        for (Organization org : childs) {
            OrganizationFindDto organizationFindDto = new OrganizationFindDto();
            organizationFindDto.setOrgNo(org.getOrgNo());
            organizationFindDto.setOrgNm(org.getOrgNm());

            if (OrganizationFindDto.parent == null) {
                this.getChild().add(organizationFindDto);
            } else {
                OrganizationFindDto.parent.getChild().add(organizationFindDto);
            }


            OrganizationFindDto.parent = this;

            System.out.println("parent : " + OrganizationFindDto.parent);
            System.out.println("child : " + getChild());
        }
    }

    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }

    public void setOrgNm(String orgNm) {
        this.orgNm = orgNm;
    }
}