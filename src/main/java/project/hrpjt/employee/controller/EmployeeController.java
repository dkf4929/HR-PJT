package project.hrpjt.employee.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import project.hrpjt.employee.dto.EmployeeFindDto;
import project.hrpjt.employee.dto.EmployeeSaveDto;
import project.hrpjt.employee.dto.EmployeeUpdateDto;
import project.hrpjt.employee.service.EmployeeService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @ResponseBody
    @PostMapping("role_adm/employees/add") //회원 저장
    public String save(@Valid EmployeeSaveDto param) {
        employeeService.save(param);
        return "저장완료";
    }

    @ResponseBody
    @GetMapping("role_emp/employees/mypage")
    public EmployeeFindDto findEmployee() {
        project.hrpjt.employee.entity.Employee employee = (project.hrpjt.employee.entity.Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //로그인한 사용자 가져오기
        return employeeService.findEmployee(employee.getId());
    }

    @ResponseBody
    @GetMapping("/role_adm/employees") //모든 회원 조회
    public Page<EmployeeFindDto> findAllEmployees(Pageable pageable) {
        return employeeService.findAll(pageable);
    }

    @ResponseBody
    @DeleteMapping("/role_adm/employees/delete") //회원 삭제
    public String deleteEmployee(Long employeeId) {
        employeeService.delete(employeeId);
        return "삭제 되었습니다.";
    }

    @ResponseBody
    @PutMapping("/role_emp/employees/edit")
    public EmployeeFindDto editInfo(@RequestBody EmployeeUpdateDto param) {
        return employeeService.edit(param);
    }
}
