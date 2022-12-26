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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final EmployeeRepository employeeRepository;
    private final EntityManager entityManager;

    public List<OrganizationFindDto> save(OrganizationSaveDto dto) {
        Organization organization = dtoToEntity(dto);

        if (dto.getParentOrgNo() != null) {
            Organization parent = organizationRepository.findByOrgNo(dto.getParentOrgNo()).orElseThrow();

            organization.updateParent(parent);

            if (dto.getChildOrgNo() != null) {
                List<Organization> child = organizationRepository.findAllByOrgNo(dto.getChildOrgNo());

                child.stream().forEach(o -> {
                    o.updateParent(organization);
                });
            }
        }

        Organization saveOrg = organizationRepository.save(organization);

        return findAll(saveOrg.getId());
    }


    public List<OrganizationFindDto> findAll(Long orgId) {
        List<Organization> organizationList = organizationRepository.findAllOrg(orgId);

        List<OrganizationFindDto> dtoList = new ArrayList<>();
        Map<Long, OrganizationFindDto> map = new HashMap<>();

        organizationList.stream()
                .forEach(o -> {
                    OrganizationFindDto organizationFindDto = new OrganizationFindDto(o);

                    if (o.getParent() != null) {
                        organizationFindDto.setParentId(o.getParent().getId());
                    }

                    map.put(organizationFindDto.getId(), organizationFindDto);


                    if (o.getParent() != null  && map.size() > 1) { // 첫번째 요소인지 판별
                        map.get(o.getParent().getId()).getChild().add(organizationFindDto);
                    } else {
                        dtoList.add(organizationFindDto);
                    }
                });

        return dtoList;
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

        List<Organization> orgList = organizationRepository.findAllOrg(null);  // 전체 조직도 조회

        List<OrganizationFindDto> dtoList = new ArrayList<>();
        Map<Long, OrganizationFindDto> map = new HashMap<>();

        orgList.stream()
                .forEach(o -> {
                    OrganizationFindDto organizationFindDto = new OrganizationFindDto(o);

                    if (o.getParent() != null) {
                        organizationFindDto.setParentId(o.getParent().getId());
                    }

                    map.put(organizationFindDto.getId(), organizationFindDto);


                    if (o.getParent() != null  && map.size() > 1) { // 첫번째 요소인지 판별
                        map.get(o.getParent().getId()).getChild().add(organizationFindDto);
                    } else {
                        dtoList.add(organizationFindDto);
                    }
                });

        return new PageImpl<>(dtoList, pageable, dtoList.size());
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

        return findOrganizerByParam(pageable);
    }
}
