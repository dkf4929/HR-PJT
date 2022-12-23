package project.hrpjt.holiday.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.hrpjt.holiday.dto.HolidaysSaveDto;
import project.hrpjt.holiday.service.HolidaysService;

@RestController
@RequiredArgsConstructor
public class HolidaysController {
    private final HolidaysService holidaysService;

    @ApiOperation(value = "휴일 입력",
        notes = "관리자가 휴일을 등록한다."
    )
    @PostMapping("/role_adm/holiday")
    public String save(@RequestBody HolidaysSaveDto param) {
        holidaysService.save(param);

        return "저장 되었습니다.";
    }
}
