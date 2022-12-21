package project.hrpjt.appointment.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import project.hrpjt.appointment.dto.AppointmentFindDto;
import project.hrpjt.appointment.dto.AppointmentParamDto;
import project.hrpjt.appointment.dto.AppointmentSaveDto;
import project.hrpjt.appointment.service.AppointmentService;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping("/role_lead/appointment")
    @ApiOperation(
            value = "발령 등록",
            notes = "발령을 등록한다.(조직장 또는 시스템 관리자 기능)")
    public AppointmentFindDto add(@RequestBody AppointmentSaveDto param) {
        return appointmentService.save(param);
    }


//  조직장 -> 본인 부서원의 발령 및 본인 부서 이동 발령만 조회.
//  시스템 관리자 -> 모든 발령 정보 조회.
    @GetMapping("/role_lead/appointment")
    @ApiOperation(
            value = "발령 내역 조회",
            notes = "직원의 발령 정보를 조회한다.")
    public Page<AppointmentFindDto> findAll(String empNo, String empNm, Pageable pageable) {
        return appointmentService.findAllByParam(empNo, empNm, pageable);
    }

    //  로그인한 직원 발령 조회
    @GetMapping("/role_emp/appointment")
    @ApiOperation(
            value = "내 발령 이력 조회",
            notes = "로그인한 직원의 발령 정보를 조회한다.")
    public Page<AppointmentFindDto> myAppointment(Pageable pageable) {
        return appointmentService.findMyAppointment(pageable);
    }

    @GetMapping("/role_lead/approve")
    @ApiOperation(
            value = "승인 대기 항목",
            notes = "관리자가 승인또는 반려할 수 있는 발령 리스트 조회"
    )
    public Page<AppointmentFindDto> approveList(Pageable pageable) {
        return appointmentService.findAllApprList(pageable);
    }

    @PutMapping("/role_lead/approve/{appId}")
    @ApiOperation(
            value = "발령 승인",
            notes = "조직장 또는 시스템 관리자/CEO가 발령 승인."
    )
    public Page<AppointmentFindDto> approve(@PathVariable Long appId, Pageable pageable) {
        return appointmentService.approve(appId, pageable);
    }
}
