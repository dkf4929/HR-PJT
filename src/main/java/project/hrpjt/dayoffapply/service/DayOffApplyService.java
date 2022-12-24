package project.hrpjt.dayoffapply.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.appointment.entity.enumeration.ApprovementStatus;
import project.hrpjt.dayoff.entity.DayOff;
import project.hrpjt.dayoff.repository.DayOffRepository;
import project.hrpjt.dayoffapply.dto.DayOffApplyFindDto;
import project.hrpjt.dayoffapply.dto.DayOffApplySaveDto;
import project.hrpjt.dayoffapply.entity.DayOffApply;
import project.hrpjt.dayoffapply.entity.enumeration.DayOffType;
import project.hrpjt.dayoffapply.repository.DayOffApplyRepository;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.employee.repository.EmployeeRepository;
import project.hrpjt.holiday.entity.Holidays;
import project.hrpjt.holiday.repository.HolidayRepository;
import project.hrpjt.organization.dto.OrganizationFindDto;
import project.hrpjt.organization.repository.OrganizationRepository;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class DayOffApplyService {
    private final DayOffApplyRepository dayOffApplyRepository;
    private final EmployeeRepository employeeRepository;
    private final HolidayRepository holidayRepository;
    private final OrganizationRepository organizationRepository;
    private final DayOffRepository dayOffRepository;

    public Page<DayOffApplyFindDto> findDayOffList(Pageable pageable) {
        Employee loginEmp = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String role = loginEmp.getRole();

        List<DayOffApply> list = new ArrayList<>();

        if (role.equals("ROLE_EMPLOYEE")) {
            list = dayOffApplyRepository.findByEmpId(loginEmp.getId());
        } else if (role.equals("ROLE_ORG_LEADER")) {
            list = dayOffApplyRepository.findByOrgIds(getOrgIds(loginEmp));
        } else {
            list = dayOffApplyRepository.findAll();
        }

        List<DayOffApplyFindDto> findList = list.stream()
                .map(c -> DayOffApplyFindDto.builder()
                        .dayOffType(c.getDayOffType())
                        .minusDays(c.getMinusDays())
                        .status(c.getStatus())
                        .endDate(c.getEndDate())
                        .startDate(c.getStartDate())
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(findList, pageable, findList.size());
    }

    public Page<DayOffApplyFindDto> findApprList(Pageable pageable) {
        Employee loginEmp = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = loginEmp.getRole();

        List<DayOffApply> list = new ArrayList<>();

        if (role.equals("ROLE_ORG_LEADER")) {
            list = dayOffApplyRepository.findApprListByOrgIds(getOrgIds(loginEmp));
        } else {
            list = dayOffApplyRepository.findApprListCEO();
        }

        List<DayOffApplyFindDto> findList = list.stream()
                .map(c -> DayOffApplyFindDto.builder()
                        .dayOffType(c.getDayOffType())
                        .minusDays(c.getMinusDays())
                        .status(c.getStatus())
                        .endDate(c.getEndDate())
                        .startDate(c.getStartDate())
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(findList, pageable, findList.size());
    }

    public DayOffApply save(DayOffApplySaveDto param) {
        return dayOffApplyRepository.save(dtoToEntity(param));
    }

    public void approve(Long id) {
        Employee loginEmp = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = loginEmp.getRole();
        DayOffApply apply = dayOffApplyRepository.findById(id).orElseThrow();

        System.out.println("apply = " + apply);

        if (role.equals("ROLE_ORG_LEADER")) {
            apply.updateStatus(ApprovementStatus.CEO_PENDING_APPR); // ceo 승인대기 상태로 변경
        } else {
            apply.updateStatus(ApprovementStatus.APPR); // 최종승인 상태로 변경

            List<Integer> years = new ArrayList<>();

            if (apply.getStartDate().getYear() == apply.getEndDate().getYear()) {
                years.add(apply.getStartDate().getYear());
            } else {
                years.add(apply.getStartDate().getYear());
                years.add(apply.getEndDate().getYear());
            }

            List<DayOff> myDayOff = dayOffRepository.findMyDayOff(apply.getEmployee(), years);

            if (myDayOff.size() > 1) {
                if (apply.getDayOffType().equals(DayOffType.ANNUAL_DAY_OFF)) {
                    myDayOff.get(0).updateAnnualDayOff(myDayOff.get(0).getAnnualDayOff() - apply.getLastYearMinusDays());
                    myDayOff.get(1).updateAnnualDayOff(myDayOff.get(1).getAnnualDayOff() - apply.getThisYearMinusDays());
                } else if (apply.getDayOffType().equals(DayOffType.SPECIAL_DAY_OFF)) {
                    myDayOff.get(0).updateSpecialDayOff(myDayOff.get(0).getSpecialDayOff() - apply.getLastYearMinusDays());
                    myDayOff.get(1).updateSpecialDayOff(myDayOff.get(1).getSpecialDayOff() - apply.getThisYearMinusDays());
                }
            } else {
                if (apply.getDayOffType().equals(DayOffType.SPECIAL_DAY_OFF)) {
                    myDayOff.get(0).updateSpecialDayOff(myDayOff.get(0).getSpecialDayOff() - apply.getMinusDays());
                } else {
                    myDayOff.get(0).updateAnnualDayOff(myDayOff.get(0).getAnnualDayOff() - apply.getMinusDays());
                }
            }
        }
    }

    private DayOffApply dtoToEntity(DayOffApplySaveDto param) {
        Employee loginEmp = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Integer> years = new ArrayList<>();

        if (param.getStartDate().getYear() == param.getEndDate().getYear()) {  // 시작일 종료일 년도 같을 경우
            years.add(param.getStartDate().getYear());
        } else { // 다를 경우 이전년도와 올해년도 휴일을 가져와야함.
            years.add(param.getStartDate().getYear());
            years.add(param.getEndDate().getYear());
        }

        List<Holidays> list = holidayRepository.findByYears(years);   // 휴일 관리 테이블에서 휴일 일정을 가져온다.
        List<LocalDate> holidays = new ArrayList<>();

        list.forEach(l -> holidays.add(l.getHoliday()));

        double minusDays;  // 올해 연차 차감일수
        double lastMinusDays; // 작년도 연차 차감일수

        List<DayOff> myDayOff = dayOffRepository.findMyDayOff(loginEmp, years);

        // 보상 휴가 또는 연차일 경우
        if (param.getDayOffType().equals(DayOffType.SPECIAL_DAY_OFF) || param.getDayOffType().equals(DayOffType.ANNUAL_DAY_OFF)) {
            if (param.getStartDate().getYear() == param.getEndDate().getYear()) {
                minusDays = Duration.between(param.getStartDate().atStartOfDay(), param.getEndDate().atStartOfDay()).toDays() + 1; // 올해 차감일수 계산
                lastMinusDays = 0;
            } else { // 휴가가 이전년도에서 다음년도까지 이어질 경우
                LocalDate startDate = param.getStartDate();
                LocalDate endDate = param.getEndDate();

                lastMinusDays = Duration.between(startDate.atStartOfDay(), startDate.withDayOfMonth(startDate.lengthOfMonth()).atStartOfDay()).toDays() + 1;
                minusDays = Duration.between(LocalDate.of(endDate.getYear(), 1, 1).atStartOfDay(), endDate.atStartOfDay()).toDays() + 1;
            }
        } else {
            if (!param.getStartDate().equals(param.getEndDate())) {
                throw new IllegalStateException("오전/오후 반차는 같은 날짜로 지정하셔야 합니다.");
            }

            minusDays = 0.5;
            lastMinusDays = 0;
        }

        // 휴일 관리 테이블에서 주말 또는 휴일(대체휴일, 공휴일, 창립기념일 등)을 조회하여 휴일을 제외한 연차감산일 계산
        for (LocalDate date = param.getStartDate(); date.isBefore(param.getEndDate().plusDays(1)); date = date.plusDays(1)) {
            if (isWeekend(date) || holidays.contains(date)) {
                if (date.getYear() != param.getEndDate().getYear()) { // 올해 차감일수인지 작년 차감일수인지에 따른 처리
                    lastMinusDays --;
                } else {
                    minusDays --;
                }
            }
        }

        // 연차 / 보상휴가는 하루 이상 사용.
        if (minusDays <= 0) {
            throw new IllegalStateException("감산일이 0일 입니다.");
        }

        if (lastMinusDays > 0) { // 신청 휴가가 전년도와 올해에 걸쳐 있는 경우 ex -> 2022-12-30 ~ 2023-01-03
            // 작년 또는 올해 연차 갯수에 따른 처리
            if (param.getDayOffType().equals(DayOffType.ANNUAL_DAY_OFF)) {
                if (myDayOff.get(0).getAnnualDayOff() - lastMinusDays < 0 || myDayOff.get(1).getAnnualDayOff() - minusDays < 0) {
                    throw new IllegalStateException("작년 또는 올해 연차 갯수가 부족합니다.");
                }
            } else {
                if (myDayOff.get(0).getSpecialDayOff() - lastMinusDays < 0 || myDayOff.get(1).getSpecialDayOff() - minusDays < 0) {
                    throw new IllegalStateException("작년 또는 올해 연차 갯수가 부족합니다.");
                }
            }
        } else {
            if (param.getDayOffType().equals(DayOffType.SPECIAL_DAY_OFF)) {
                if (myDayOff.get(0).getSpecialDayOff() - minusDays < 0) {
                    throw new IllegalStateException("올해 연차가 부족합니다.");
                }
            } else {
                if (myDayOff.get(0).getAnnualDayOff() - minusDays < 0) {
                    throw new IllegalStateException("올해 연차가 부족합니다.");
                }
            }
        }

        return DayOffApply.builder()
                .dayOffType(param.getDayOffType())
                .startDate(param.getStartDate())
                .endDate(param.getEndDate())
                .employee((Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .minusDays(minusDays + lastMinusDays)
                .thisYearMinusDays(minusDays)
                .lastYearMinusDays(lastMinusDays)
                .build();
    }

    private boolean isWeekend(LocalDate date) { // 주말 여부 판단
        Calendar cal = Calendar.getInstance();
        cal.setTime(Date.valueOf(date));
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
    }

    private List<Long> getOrgIds(Employee loginEmp) {
        List<OrganizationFindDto> allOrg = organizationRepository.findAllOrg(loginEmp.getOrganization().getId());  // 조직장의 하위 조직 정보 추출

        List<Long> orgIds = new ArrayList<>();

        allOrg.stream().forEach(o -> {
            orgIds.add(o.getId());

            o.getChild().stream().forEach(c -> {
                orgIds.add(c.getId());
            });
        });
        return orgIds;
    }
}
