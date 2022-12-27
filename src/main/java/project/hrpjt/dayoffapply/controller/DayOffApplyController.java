package project.hrpjt.dayoffapply.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import project.hrpjt.dayoffapply.dto.DayOffApplyFindDto;
import project.hrpjt.dayoffapply.dto.DayOffApplySaveDto;
import project.hrpjt.dayoffapply.service.DayOffApplyService;
import project.hrpjt.mail.MailSender;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DayOffApplyController {
    private final DayOffApplyService dayOffApplyService;

    @ApiOperation(value = "근태 신청")
    @PostMapping("/role_emp/day_off")
    public String save(@RequestBody DayOffApplySaveDto param) {
        dayOffApplyService.save(param);

        return "신청이 완료 되었습니다.";
    }

    @ApiOperation(
            value = "근태 신청내역",
            notes = "권한에 따른 근태 신청내역 확인"
    )
    @GetMapping("/role_emp/day_off")
    public Page<DayOffApplyFindDto> findDayOffApply(Pageable pageable) {
        return dayOffApplyService.findDayOffList(pageable);
    }

    @ApiOperation(
            value = "결재 문서 조회",
            notes = "결재 대기중인 근태를 조회"
    )
    @GetMapping("/role_lead/day_off/apply")
    public Page<DayOffApplyFindDto> findApprList(Pageable pageable) {
        return dayOffApplyService.findApprList(pageable);
    }

    @ApiOperation(
            value = "근태 문서 결재",
            notes = "결재 대기중인 문서를 조직장 또는 ceo가 승인한다."
    )
    @PutMapping("/role_lead/day_off/apply/{id}")
    public String approve(Long id) {
        dayOffApplyService.approve(id);

        return "승인 되었습니다.";
    }
}
