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

    @PostMapping("/role_adm/organization/add") //조직 추가
    public Organization save(OrganizationSaveDto dto) {
        return organizationService.save(dto);
    }

    @GetMapping("/role_emp/organization") // 조직도 조회
    public Page<OrganizationFindDto> findAll(@RequestBody OrganizationFindParamDto dto, Pageable pageable) {
        return organizationService.findAll(dto, pageable);
    }

//  조직원 조회
//  employee, org_leader -> 본인 조직원만 조회 가능
//  sys_admin, ceo -> 모든 조직의 조직원 조회 가능
    @GetMapping("/role_emp/organization/employees")
    public Page<OrganizerFindDto> findEmployee(@RequestBody OrganizerFindParamDto param, Pageable pageable) {
        return organizationService.findOrganizerByParam(param, pageable);
    }
}
