package project.hrpjt.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class OrganizationSaveDto {
    private String orgNo;
    private String orgNm;
    private Long parentOrgId;
    private LocalDate startDate;
    private LocalDate endDate;

}
