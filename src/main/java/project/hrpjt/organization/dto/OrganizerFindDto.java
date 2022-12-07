package project.hrpjt.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OrganizerFindDto {
    private String orgNo;
    private String orgNm;
    private String empNo;
    private String empNm;
    private String role;
}
