package project.hrpjt.organization.repository;

import org.springframework.data.domain.Pageable;
import project.hrpjt.organization.dto.OrganizationFindDto;
import project.hrpjt.organization.dto.OrganizationFindParamDto;
import project.hrpjt.organization.dto.OrganizerFindDto;
import project.hrpjt.organization.dto.OrganizerFindParamDto;
import project.hrpjt.organization.entity.Organization;

import java.util.List;
import java.util.Set;

public interface OrganizationRepositoryCustom {
    public List<OrganizationFindDto> findAllOrg(OrganizationFindParamDto dto);

    public List<OrganizerFindDto> findOrganizerByParam();

    public List<Organization> findAllChild(Long orgId);
}
