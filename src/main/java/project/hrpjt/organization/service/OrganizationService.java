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
import project.hrpjt.exception.NoAuthorityException;
import project.hrpjt.organization.dto.*;
import project.hrpjt.organization.entity.Organization;
import project.hrpjt.organization.repository.OrganizationRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationService {
    private final OrganizationRepository organizationRepository;

    public Organization save(OrganizationSaveDto dto) {
        Organization organization = dtoToEntity(dto);

        if (dto.getParentOrgId() != null) {
            Organization parent = organizationRepository.findById(dto.getParentOrgId()).get();
            organization.updateParent(parent);
            parent.addChild(organization);
        }

        return organizationRepository.save(organization);
    }


    public Page<OrganizationFindDto> findAll(OrganizationFindParamDto dto, Pageable pageable) {
        return new PageImpl<>(organizationRepository.findAllOrg(dto).stream()
                .map(o -> OrganizationFindDto.builder()  // entity -> dto
                        .parentOrgNm(o.getParent() == null ? null : o.getParent().getOrgNm())
                        .parentOrgNo(o.getParent() == null ? null : o.getParent().getOrgNo())
                        .orgNm(o.getOrgNm())
                        .orgNo(o.getOrgNo())
                        .childOrgNo(o.getChildren() == null ? null : o.getChildren().stream().map(chi -> chi.getOrgNo()).collect(Collectors.toList()))
                        .childOrgNm(o.getChildren() == null ? null : o.getChildren().stream().map(chi -> chi.getOrgNm()).collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList())); // dto -> page<dto>
    }


    public Page<OrganizerFindDto> findOrganizerByParam(OrganizerFindParamDto param, Pageable pageable) {
        Employee loginEmp = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //권한 확인을 위해 로그인한 사용자 추출

        boolean authYn = authCheck(loginEmp, param);

        if (!authYn) {
            throw new NoAuthorityException("해당 조직에 접근할 수 있는 권한이 없습니다.");
        }

        return new PageImpl<>(organizationRepository.findOrganizerByParam(param));
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
                (!param.getOrgNo().equals(loginEmp.getOrganization().getOrgNo()) ||
                        (!param.getOrgNm().equals(loginEmp.getOrganization().getOrgNm())))) {
            return false;
        } else {
            return true;
        }
    }
}
