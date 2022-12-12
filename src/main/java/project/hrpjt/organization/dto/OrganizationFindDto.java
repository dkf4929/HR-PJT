package project.hrpjt.organization.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import org.springframework.stereotype.Component;
import project.hrpjt.organization.entity.Organization;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@Component
@Getter
@EqualsAndHashCode(of = {"orgNo", "orgNm"})
@ToString(of = {"orgNo", "orgNm"})
public class OrganizationFindDto {
    private String orgNo;
    private String orgNm;
    private Set<OrganizationFindDto> child = new HashSet<>();

    @JsonIgnore
    private OrganizationFindDto parent;

    @Builder
    public OrganizationFindDto(Organization organization, Set<Organization> childs, OrganizationFindDto parent) {
        this.orgNo = organization.getOrgNo();
        this.orgNm = organization.getOrgNm();

        for (Organization org : childs) {
            OrganizationFindDto organizationFindDto = new OrganizationFindDto();
            organizationFindDto.setOrgNo(org.getOrgNo());
            organizationFindDto.setOrgNm(org.getOrgNm());

            if (this.parent == null) {
                this.getChild().add(organizationFindDto);
            } else {
                this.parent.getChild().add(organizationFindDto);
            }
        }

        this.parent = this;

        System.out.println("parent = " + this.hashCode() + " child : " + this.getChild().hashCode());
        System.out.println("parent = " + this + " child : " + this.getChild());
    }

    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }

    public void setParent(OrganizationFindDto parent) {
        this.parent = parent;
    }

    public void setOrgNm(String orgNm) {
        this.orgNm = orgNm;
    }
}