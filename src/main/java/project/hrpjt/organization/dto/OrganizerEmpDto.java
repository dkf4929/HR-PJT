package project.hrpjt.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class OrganizerEmpDto {
    private String empNo;
    private String empNm;
    private String role;
}
