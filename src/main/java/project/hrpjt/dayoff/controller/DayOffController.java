package project.hrpjt.dayoff.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.hrpjt.dayoff.service.DayOffService;

@RestController
@RequiredArgsConstructor
public class DayOffController {
    private final DayOffService dayOffService;

    @ApiOperation(
            value = "연차촉진 메일 발송",
            notes = "현재년도 기준 잔여연차가 10일 이상 남은 직원들에게 연차사용 촉진 메일 발송"
    )
    @PostMapping("/role-adm/day-off/mail")
    public String sendMail() {
        dayOffService.sendMail();

        return "메일 발송 완료";
    }
}
