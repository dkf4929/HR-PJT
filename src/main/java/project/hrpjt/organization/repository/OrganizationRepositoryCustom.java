package project.hrpjt.organization.repository;

import org.springframework.data.domain.Pageable;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.organization.dto.OrganizationFindDto;
import project.hrpjt.organization.dto.OrganizationFindParamDto;
import project.hrpjt.organization.dto.OrganizerFindDto;
import project.hrpjt.organization.dto.OrganizerFindParamDto;
import project.hrpjt.organization.entity.Organization;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrganizationRepositoryCustom {
    public List<OrganizationFindDto> findAllOrg(OrganizationFindParamDto dto);

    public List<OrganizerFindDto> findOrganizerByParam(OrganizerFindParamDto param);

    public List<Organization> findAllChild(Long orgId);

    public int updateEndDate(List<Organization> child);

    public Optional<Organization> findOrgById(Long id);

    public Optional<Organization> findOrgByEmp(Employee employee);

    public Optional<Organization> findByOrgNo(String orgNo);

    public List<Organization> findAllByOrgNo(List<String> orgNoList);
}
