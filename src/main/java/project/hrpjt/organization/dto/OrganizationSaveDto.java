package project.hrpjt.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class OrganizationSaveDto {
    private String orgNo;
    private String orgNm;
    private String parentOrgNo;
    private List<String> childOrgNo = new ArrayList<>();
    private LocalDate startDate;
    private LocalDate endDate;

}
