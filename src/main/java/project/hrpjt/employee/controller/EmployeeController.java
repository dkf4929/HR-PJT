package project.hrpjt.employee.controller;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.hrpjt.employee.dto.EmployeeFindDto;
import project.hrpjt.employee.dto.EmployeeSaveDto;
import project.hrpjt.employee.dto.EmployeeUpdateDto;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.employee.service.EmployeeService;

@RestController
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @ApiOperation(value = "회원 저장")
    @PostMapping("role_adm/employees") //회원 저장
    public String save(@RequestBody @Valid EmployeeSaveDto param) {
        employeeService.save(param);
        return "저장완료";
    }

    @ApiOperation(value = "회원 검색")
    @GetMapping("role_emp/employees")
    public EmployeeFindDto findEmployee() {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //로그인한 사용자 가져오기
        return employeeService.findEmployee(employee.getId());
    }

    @ApiOperation(value = "모든 회원 조회")
    @GetMapping("/role_adm/employees") //모든 회원 조회
    public Page<EmployeeFindDto> findAllEmployees(Pageable pageable) {
        return employeeService.findAll(pageable);
    }

    // 시스템 관리자는 삭제 불가.
    @ApiOperation(value = "회원 삭제")
    @DeleteMapping("/role_adm/employees") //회원 삭제
    public String deleteEmployee(String empNo) {
        employeeService.delete(empNo);
        return "삭제 되었습니다.";
    }


//     1. 패스워드를 제외한 항목은 유저 권한을 가진 직원이 수정 불가능함.
//     2. 관리자 권한을 가진 직원이 해당 직원을 같은 권한으로 설정할 수 없음.
//     3. 변경 가능한 권한 항목 -> sys_admin(org_leader 권한 부여 가능), ceo(sys_admin 권한 부여 가능)
//     4. 개인 정보는 해당 사원의 부서장 또는 시스템 관리자가 변경 가능. 사번, 성명, 조직은 시스템 관리자만 변경 가능.
    @ApiOperation(value = "회원 정보 수정")
    @PutMapping("/role_emp/employees")
    public EmployeeFindDto editInfo(@RequestBody EmployeeUpdateDto param) {
        return employeeService.edit(param);
    }
}
