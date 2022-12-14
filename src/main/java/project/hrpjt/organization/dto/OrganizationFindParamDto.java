package project.hrpjt.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class OrganizationFindParamDto {
    private Long orgId;
}
