package project.hrpjt.organization.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.employee.repository.EmployeeRepository;
import project.hrpjt.exception.OrgDeleteException;
import project.hrpjt.organization.dto.*;
import project.hrpjt.organization.entity.Organization;
import project.hrpjt.organization.repository.OrganizationRepository;

import java.time.LocalDate;
import java.util.List;

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
//            parent.addChild(organization);

            if (dto.getChildOrgId() != null) {
                List<Organization> child = organizationRepository.findAllById(dto.getChildOrgId());

                for (Organization org : child) {
                    org.updateParent(organization);
                }
            }
        }

        return organizationRepository.save(organization);
    }


    public Page<OrganizationFindDto> findAll(OrganizationFindParamDto dto, Pageable pageable) {
        List<OrganizationFindDto> list = organizationRepository.findAllOrg(dto);

        return new PageImpl<>(list, pageable, list.size());
    }


    public Page<OrganizerFindDto> findOrganizerByParam(Pageable pageable) {
//        Employee loginEmp = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //권한 확인을 위해 로그인한 사용자 추출
//
//        boolean authYn = authCheck(loginEmp, param);
//
//        if (!authYn) {
//            throw new NoAuthorityException("해당 조직에 접근할 수 있는 권한이 없습니다.");
//        }

        List<OrganizerFindDto> list = organizationRepository.findOrganizerByParam();

        return new PageImpl<>(list, pageable, list.size());
    }


    public Page<OrganizationFindDto> close(OrganizationFindParamDto dto, Pageable pageable) {
//        Organization organization = organizationRepository.findById(orgId).orElseThrow();

        List<Organization> allChild = organizationRepository.findAllChild(dto.getOrgId());

        for (Organization org : allChild) {
            org.updateEndDate(LocalDate.now().minusDays(1));  // 부모 조직과 하위 조직의 종료일을 오늘 일자 -1일로 업데이트
        }

        List<OrganizationFindDto> orgList = organizationRepository.findAllOrg(OrganizationFindParamDto.builder().build());  // 전체 조직도 조회

        return new PageImpl<>(orgList, pageable, orgList.size());
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
}