package project.hrpjt.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
public class OrganizationFindDto {
    private String parentOrgNo;
    private String parentOrgNm;
    private String orgNo;
    private String orgNm;
    private List<String> childOrgNo;
    private List<String> childOrgNm;

    @Builder
    public OrganizationFindDto(String parentOrgNo, String parentOrgNm, String orgNo, String orgNm, List<String> childOrgNo, List<String> childOrgNm) {
        this.parentOrgNo = parentOrgNo;
        this.parentOrgNm = parentOrgNm;
        this.orgNo = orgNo;
        this.orgNm = orgNm;
        this.childOrgNo = childOrgNo;
        this.childOrgNm = childOrgNm;
    }
}
