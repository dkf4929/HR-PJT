package project.hrpjt.organization.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.employee.repository.EmployeeRepository;
import project.hrpjt.exception.NoAuthorityException;
import project.hrpjt.exception.OrgDeleteException;
import project.hrpjt.organization.dto.*;
import project.hrpjt.organization.entity.Organization;
import project.hrpjt.organization.repository.OrganizationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final EmployeeRepository employeeRepository;

    public Organization save(OrganizationSaveDto dto) {
        Organization organization = dtoToEntity(dto);

        if (dto.getParentOrgId() != null) {
            Organization parent = organizationRepository.findById(dto.getParentOrgId()).orElseThrow();
            organization.updateParent(parent);
            parent.addChild(organization);
        }

        return organizationRepository.save(organization);
    }


    public Page<OrganizationFindDto> findAll(OrganizationFindParamDto dto, Pageable pageable) {
        List<OrganizationFindDto> list = organizationRepository.findAllOrg(dto);

        return new PageImpl<>(list, pageable, list.size());
    }


    public Page<OrganizerFindDto> findOrganizerByParam(OrganizerFindParamDto param, Pageable pageable) {
        Employee loginEmp = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //권한 확인을 위해 로그인한 사용자 추출

        boolean authYn = authCheck(loginEmp, param);

        if (!authYn) {
            throw new NoAuthorityException("해당 조직에 접근할 수 있는 권한이 없습니다.");
        }

        List<OrganizerFindDto> list = organizationRepository.findOrganizerByParam(param);

        return new PageImpl<>(list, pageable, list.size());
    }


    public Page<OrganizationFindDto> delete(Long orgId, Pageable pageable) {
        Organization organization = organizationRepository.findById(orgId).orElseThrow();
        Long count = employeeRepository.countInOfficeEmp(organization); // 해당 조직의 재직중인 사원 수 count

        if (count > 0) {
            throw new OrgDeleteException("해당 조직에 재직중인 사원이 있습니다.");
        }

//        organizationRepository.delete(organization);
//
//        List<Organization> orgList = organizationRepository.findAllOrg(null);
//
//        List<OrganizationFindDto> list = getCollect(orgList);

        return null;
//        return new PageImpl<>(list, pageable, list.size());
    }

    private Organization dtoToEntity(OrganizationSaveDto dto) {
        return Organization.builder()
                .orgNm(dto.getOrgNm())
                .orgNo(dto.getOrgNo())
                .endDate(dto.getEndDate())
                .startDate(dto.getStartDate())
                .build();
    }

    private boolean authCheck(Employee loginEmp, OrganizerFindParamDto param) {
        String role = loginEmp.getRole();

        if ((role.equals("ROLE_EMPLOYEE") || role.equals("ROLE_ORG_LEADER")) &&
                (param.getOrgNo() != null && !param.getOrgNo().equals(loginEmp.getOrganization().getOrgNo()) ||
                        (param.getOrgNm() != null && !param.getOrgNm().equals(loginEmp.getOrganization().getOrgNm())))) {
            return false;
        } else {
            return true;
        }
    }

//    private List<OrganizationFindDto> getCollect(List<Organization> orgList) {
//        return orgList.stream()
//                .map(o -> OrganizationFindDto.builder()  // entity -> dto
//                        .organization(o)
//
//                        .build())
//                .collect(Collectors.toList());
//    }
}