package project.hrpjt.organization.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final EmployeeRepository employeeRepository;
    private final EntityManager entityManager;

    public Organization save(OrganizationSaveDto dto) {
        Organization organization = dtoToEntity(dto);

        if (dto.getParentOrgId() != null) {
            Organization parent = organizationRepository.findById(dto.getParentOrgId()).orElseThrow();

            organization.updateParent(parent);

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
        Employee loginEmp = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //권한 확인을 위해 로그인한 사용자 추출

        Organization organization = organizationRepository.findOrgByEmp(loginEmp).orElseThrow(() -> {
            throw new IllegalStateException("현재 소속된 조직이 없습니다.");
        });

        String role = loginEmp.getRole();

        List<OrganizerFindDto> list = new ArrayList<>();

        list = getOrganizer(organization, role, list);

        return new PageImpl<>(list, pageable, list.size());
    }

    public Page<OrganizerFindDto> edit(OrganizationUpdateDto dto, Pageable pageable) {
        Employee loginEmp = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Organization organization = organizationRepository.findById(dto.getUpdateOrgId()).orElseThrow(() -> {
            throw new IllegalStateException("존재 하지 않는 조직입니다.");
        });

        String role = loginEmp.getRole();

        return getOrganizerFindDtos(dto, pageable, organization, role);
    }



    public Page<OrganizationFindDto> close(OrganizationFindParamDto dto, Pageable pageable) {
        List<Organization> allChild = organizationRepository.findAllChild(dto.getOrgId());

        int empCnt = 0;

        for (Organization organization : allChild) {
            empCnt += organization.getEmployees().size();
        }

        if (empCnt == 0) {
            organizationRepository.updateEndDate(allChild);
        } else {
            throw new IllegalStateException("해당 부서 또는 하위 부서에 소속된 사원이 있습니다.");
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

    private List<OrganizerFindDto> getOrganizer(Organization organization, String role, List<OrganizerFindDto> list) {
        if (role.equals("ROLE_EMPLOYEE")) {
            OrganizerFindDto dto = OrganizerFindDto.builder()
                    .orgNo(organization.getOrgNo())
                    .orgNm(organization.getOrgNm())
                    .empList(organization.getEmployees().stream()
                            .map(o -> OrganizerEmpDto.builder()
                                    .empNo(o.getEmpNo())
                                    .empNm(o.getEmpNm())
                                    .role(o.getRole())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            list.add(dto);
        } else if (role.equals("ROLE_ORG_LEADER")) {
            list = organizationRepository.findOrganizerByParam(OrganizerFindParamDto.builder()
                    .orgId(organization.getId())
                    .build());
        } else if (role.equals("ROLE_SYS_ADMIN")) {
            list = organizationRepository.findOrganizerByParam(new OrganizerFindParamDto());
        }
        return list;
    }

    private Page<OrganizerFindDto> getOrganizerFindDtos(OrganizationUpdateDto dto, Pageable pageable, Organization organization, String role) {
        if (role.equals("ROLE_SYS_ADMIN")) {
            if (dto.getOrgNo() != null) {
                organization.updateOrgNo(dto.getOrgNo());
            }
            if (dto.getOrgNm() != null) {
                organization.updateOrgNm(dto.getOrgNm());
            }
            if (dto.getParentId() != null) {
                Organization parent = organizationRepository.findById(dto.getParentId()).orElseThrow(() -> {
                    throw new IllegalStateException("존재 하지 않는 조직입니다.");
                });

                organization.updateParent(parent);  // 해당 조직의 부모를 업데이트.
                parent.getChildren().add(organization); // 부모 조직에 자식 조직으로 추가.
            }
        } else if (role.equals("ROLE_ORG_LEADER") && (dto.getOrgNo() != null ||
                dto.getOrgNm() != null || dto.getParentId() != null)) {
            throw new NoAuthorityException("해당 필드에 대한 수정 권한이 없습니다.");
        }

        // 공통 권한.
        if (dto.getAddEmpIds() != null) {
            List<Employee> employees = employeeRepository.findAllById(dto.getAddEmpIds());

            employees.forEach(e -> {
                if (e.getOrganization() != null) {
                    throw new IllegalStateException("조직에 소속되어 있는 직원이 있습니다. "); // 겸직 불가능함.
                }
            });

            organization.getEmployees().add(employeeRepository.findAllById(dto.getAddEmpIds()).iterator().next()); // 조직에 직원 추가
        }
        if (dto.getDeleteEmpIds() != null) {
            organization.getEmployees().remove(employeeRepository.findAllById(dto.getDeleteEmpIds()).iterator().next()); // 직원 삭제
        }

        return findOrganizerByParam(pageable);
    }
}
