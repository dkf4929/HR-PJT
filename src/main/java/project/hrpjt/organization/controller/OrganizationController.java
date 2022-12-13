package project.hrpjt.organization.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.exception.NoAuthorityException;
import project.hrpjt.organization.dto.*;
import project.hrpjt.organization.entity.Organization;
import project.hrpjt.organization.service.OrganizationService;

@RestController
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping("/role_adm/organization") //조직 추가
    public Organization save(@RequestBody OrganizationSaveDto dto) {
        return organizationService.save(dto);
    }

    @GetMapping("/role_emp/organization") // 조직도 조회
    public Page<OrganizationFindDto> findAll(@RequestBody OrganizationFindParamDto dto, Pageable pageable) {
        return organizationService.findAll(dto, pageable);
    }

//  조직원 조회
    @GetMapping("/role_emp/organization/employees")
    public Page<OrganizerFindDto> findEmployee(Pageable pageable) {
        return organizationService.findOrganizerByParam(pageable);
    }

//  조직 폐쇄(종료일을 현재일로 update)
    @DeleteMapping("/role_adm/organization")
    public Page<OrganizationFindDto> close(Long orgId, Pageable pageable) {
        return organizationService.close(orgId, pageable);
    }
}
