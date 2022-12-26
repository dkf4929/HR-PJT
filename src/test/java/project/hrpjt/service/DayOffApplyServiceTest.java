package project.hrpjt.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.appointment.entity.enumeration.ApprovementStatus;
import project.hrpjt.dayoff.entity.DayOff;
import project.hrpjt.dayoff.repository.DayOffRepository;
import project.hrpjt.dayoffapply.dto.DayOffApplyFindDto;
import project.hrpjt.dayoffapply.dto.DayOffApplySaveDto;
import project.hrpjt.dayoffapply.entity.DayOffApply;
import project.hrpjt.dayoffapply.entity.enumeration.DayOffType;
import project.hrpjt.dayoffapply.service.DayOffApplyService;
import project.hrpjt.employee.service.CustomEmployeeDetailsService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
public class DayOffApplyServiceTest {
    @Autowired DayOffApplyService dayOffApplyService;
    @Autowired DayOffRepository dayOffRepository;
    @Autowired CustomEmployeeDetailsService employeeDetailsService;

    @BeforeEach
    void each() throws Exception {
        login("ADMIN");
    }

    @Test
    @DisplayName("근태신청 (2022.12.25 ~ 2023.01.04")
    void apply() {
        DayOffApply saved = dayOffApplyService.save(DayOffApplySaveDto.builder()
                .dayOffType(DayOffType.ANNUAL_DAY_OFF)
                .startDate(LocalDate.of(2022, 12, 25))
                .endDate(LocalDate.of(2023, 01, 04))
                .build());

        assertThat(saved.getLastYearMinusDays()).isEqualTo(4.0); // 2022년 연차차감일 4일
        assertThat(saved.getThisYearMinusDays()).isEqualTo(2.0); // 2023년 연차차감일 2일

        Page<DayOffApplyFindDto> dayOffList = dayOffApplyService.findDayOffList(PageRequest.of(1, 10));

        DayOffApplyFindDto build = DayOffApplyFindDto.builder()
                .minusDays(saved.getMinusDays())
                .status(saved.getStatus())
                .dayOffType(saved.getDayOffType())
                .endDate(saved.getEndDate())
                .startDate(saved.getStartDate())
                .build();

        assertThat(dayOffList.getContent()).contains(build);

        login("ORG_LEADER");

        List<DayOffApplyFindDto> content = dayOffApplyService.findApprList(PageRequest.of(1, 10)).getContent();

        assertThat(content).contains(build);  // 조직장 승인 리스트에 인사부 소속 직원의 근태신청이 있는지 확인.

        dayOffApplyService.approve(saved.getId()); // 조직장 승인

        assertThat(saved.getStatus()).isEqualTo(ApprovementStatus.CEO_PENDING_APPR);  // 조직장 승인 상태인지 확인

        login("CEO"); // ceo 권한으로 로그인

        dayOffApplyService.approve(saved.getId()); // ceo 승인

        List<Integer> years = new ArrayList<>();
        years.add(2022);
        years.add(2023);

        List<DayOff> myDayOff = dayOffRepository.findMyDayOff(saved.getEmployee(), years);

        assertThat(saved.getStatus()).isEqualTo(ApprovementStatus.APPR);
        assertThat(myDayOff.get(0).getAnnualDayOff()).isEqualTo(16); // 이전년도 차감일 4일
        assertThat(myDayOff.get(1).getAnnualDayOff()).isEqualTo(18); // 올해 차감일 2일
    }

    @Test
    @DisplayName("근태신청 (2023.01.04 ~ 2023.01.10")
    void apply2() {
        DayOffApply saved = dayOffApplyService.save(DayOffApplySaveDto.builder()
                .dayOffType(DayOffType.ANNUAL_DAY_OFF)
                .startDate(LocalDate.of(2023, 01, 04))
                .endDate(LocalDate.of(2023, 01, 10))
                .build());

        assertThat(saved.getLastYearMinusDays()).isEqualTo(0);
        assertThat(saved.getThisYearMinusDays()).isEqualTo(5.0); // 2023년 연차차감일 5일

        Page<DayOffApplyFindDto> dayOffList = dayOffApplyService.findDayOffList(PageRequest.of(1, 10));

        DayOffApplyFindDto build = DayOffApplyFindDto.builder()
                .minusDays(saved.getMinusDays())
                .status(saved.getStatus())
                .dayOffType(saved.getDayOffType())
                .endDate(saved.getEndDate())
                .startDate(saved.getStartDate())
                .build();

        assertThat(dayOffList.getContent()).contains(build);

        login("ORG_LEADER");

        List<DayOffApplyFindDto> content = dayOffApplyService.findApprList(PageRequest.of(1, 10)).getContent();

        assertThat(content).contains(build);  // 조직장 승인 리스트에 인사부 소속 직원의 근태신청이 있는지 확인.

        dayOffApplyService.approve(saved.getId()); // 조직장 승인

        assertThat(saved.getStatus()).isEqualTo(ApprovementStatus.CEO_PENDING_APPR);  // 조직장 승인 상태인지 확인

        login("CEO"); // ceo 권한으로 로그인

        dayOffApplyService.approve(saved.getId()); // ceo 승인

        List<Integer> years = new ArrayList<>();
        years.add(2023);

        List<DayOff> myDayOff = dayOffRepository.findMyDayOff(saved.getEmployee(), years);

        assertThat(saved.getStatus()).isEqualTo(ApprovementStatus.APPR);
        assertThat(myDayOff.get(0).getAnnualDayOff()).isEqualTo(15); // 올해 차감일 5일
    }

    @Test
    @DisplayName("근태신청(반차)")
    void apply3() {
        DayOffApply saved = dayOffApplyService.save(DayOffApplySaveDto.builder()
                .dayOffType(DayOffType.MORNING_DAY_OFF)
                .startDate(LocalDate.of(2023, 01, 04))
                .endDate(LocalDate.of(2023, 01, 04))
                .build());

        assertThat(saved.getLastYearMinusDays()).isEqualTo(0);
        assertThat(saved.getThisYearMinusDays()).isEqualTo(0.5); // 2023년 연차차감일 5일

        Page<DayOffApplyFindDto> dayOffList = dayOffApplyService.findDayOffList(PageRequest.of(1, 10));

        DayOffApplyFindDto build = DayOffApplyFindDto.builder()
                .minusDays(saved.getMinusDays())
                .status(saved.getStatus())
                .dayOffType(saved.getDayOffType())
                .endDate(saved.getEndDate())
                .startDate(saved.getStartDate())
                .build();

        assertThat(dayOffList.getContent()).contains(build);

        login("ORG_LEADER");

        List<DayOffApplyFindDto> content = dayOffApplyService.findApprList(PageRequest.of(1, 10)).getContent();

        assertThat(content).contains(build);  // 조직장 승인 리스트에 인사부 소속 직원의 근태신청이 있는지 확인.

        dayOffApplyService.approve(saved.getId()); // 조직장 승인

        assertThat(saved.getStatus()).isEqualTo(ApprovementStatus.CEO_PENDING_APPR);  // 조직장 승인 상태인지 확인

        login("CEO"); // ceo 권한으로 로그인

        dayOffApplyService.approve(saved.getId()); // ceo 승인

        List<Integer> years = new ArrayList<>();
        years.add(2023);

        List<DayOff> myDayOff = dayOffRepository.findMyDayOff(saved.getEmployee(), years);

        assertThat(saved.getStatus()).isEqualTo(ApprovementStatus.APPR);
        assertThat(myDayOff.get(0).getAnnualDayOff()).isEqualTo(19.5); // 올해 차감일 0.5일
    }

    @Test
    @DisplayName("보유 연차보다 많은 일 수 신청 -> error")
    void applyError() {
        DayOffApplySaveDto build = DayOffApplySaveDto.builder()
                .startDate(LocalDate.of(2023, 01, 04))
                .endDate(LocalDate.of(2023, 01, 05))
                .dayOffType(DayOffType.SPECIAL_DAY_OFF)
                .build();

        assertThatThrownBy(() -> dayOffApplyService.save(build)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("반차 신청 시 같은 일자로 신청 x -> error")
    void applyError2() {
        DayOffApplySaveDto build = DayOffApplySaveDto.builder()
                .startDate(LocalDate.of(2023, 01, 04))
                .endDate(LocalDate.of(2023, 01, 05))
                .dayOffType(DayOffType.MORNING_DAY_OFF)
                .build();

        assertThatThrownBy(() -> dayOffApplyService.save(build)).isInstanceOf(IllegalStateException.class);
    }

    private void login(String userName) {
        UserDetails userDetails = employeeDetailsService.loadUserByUsername(userName);
        userDetails.getAuthorities();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
