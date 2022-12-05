package project.hrpjt.employee.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.hrpjt.employee.dto.EmployeeFindDto;
import project.hrpjt.employee.dto.EmployeeSaveDto;
import project.hrpjt.employee.dto.EmployeeUpdateDto;
import project.hrpjt.employee.service.EmployeeService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @ResponseBody
    @PostMapping("/add") //회원 저장
    public String save(@Valid EmployeeSaveDto param) {
        employeeService.save(param);
        return "저장완료";
    }

    @ResponseBody
    @GetMapping("/mypage")
    public EmployeeFindDto findEmployee() {
        project.hrpjt.employee.entity.Employee employee = (project.hrpjt.employee.entity.Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //로그인한 사용자 가져오기
        return employeeService.findEmployee(employee.getId());
    }

    @ResponseBody
    @GetMapping() //모든 회원 조회(관리자)
    public Page<EmployeeFindDto> findAllEmployees(Pageable pageable) {
        return employeeService.findAll(pageable);
    }

    @ResponseBody
    @DeleteMapping("/delete") //회원 삭제(관리자) - 같은 권한을 가진 관리자는 삭제 불가.
    public String deleteEmployee(Long employeeId) {
        employeeService.delete(employeeId);
        return "삭제 되었습니다.";
    }

    @ResponseBody
    @PutMapping("/edit")
    public EmployeeFindDto editInfo(@RequestBody EmployeeUpdateDto param) {
        return employeeService.edit(param);
    }
}
