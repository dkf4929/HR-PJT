package project.hrpjt.organization.repository;

import project.hrpjt.organization.dto.OrganizationFindParamDto;
import project.hrpjt.organization.dto.OrganizerFindDto;
import project.hrpjt.organization.dto.OrganizerFindParamDto;
import project.hrpjt.organization.entity.Organization;

import java.util.List;

public interface OrganizationRepositoryCustom {
    public List<Organization> findAllOrg(OrganizationFindParamDto dto);

    public List<OrganizerFindDto> findOrganizerByParam(OrganizerFindParamDto dto);
}