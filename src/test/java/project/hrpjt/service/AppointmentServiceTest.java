package project.hrpjt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.appointment.dto.AppointmentFindDto;
import project.hrpjt.appointment.dto.AppointmentSaveDto;
import project.hrpjt.appointment.entity.Appointment;
import project.hrpjt.appointment.entity.enumeration.ApprovementStatus;
import project.hrpjt.appointment.entity.enumeration.AppointmentType;
import project.hrpjt.appointment.repository.AppointmentRepository;
import project.hrpjt.appointment.service.AppointmentService;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AppointmentServiceTest {
    @Autowired AppointmentService appointmentService;
    @Autowired AppointmentRepository appointmentRepository;
    @Autowired UserDetailsService userDetailsService;
    @Autowired EntityManager entityManager;

    @BeforeEach
    void each() throws Exception {
        UserDetails userDetails = userDetailsService.loadUserByUsername("ADMIN");
        userDetails.getAuthorities();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("발령 history 조회")
    void appointmentList() throws Exception {
        Page<AppointmentFindDto> all = appointmentService.findAllByParam(null, null, PageRequest.of(1, 50));

        System.out.println("size = " + all.getContent().size());
    }

    // 1. 부서원 발령을 등록한다.
    // 2. 해당 부서원의 부서장의 발령 승인 리스트를 조회한다. (조직장의 승인리스트 -> 해당 조직 또는 하위 조직으로의 이동 발령 또는 하위 조직원의 발령)
    @Test
    @DisplayName("발령 등록")
    void addAppointment() throws Exception {
        AppointmentSaveDto dto = AppointmentSaveDto.builder()
                .empNo("EMPLOYEE")   // 인사 1팀 소속
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .type(AppointmentType.DISEASE_LEAVE)
                .build();


        AppointmentFindDto savedDto = appointmentService.save(dto);

        // 인사부 조직장으로 로그인 했다고 가정
        UserDetails userDetails = userDetailsService.loadUserByUsername("ORG_LEADER");
        userDetails.getAuthorities();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Page<AppointmentFindDto> all = appointmentService.findAllApprList(PageRequest.of(1, 50));

        assertThat(all.getContent()).contains(savedDto);
    }

    //  1. 조직장이 조직장 하위 조직 발령 또는 부서원의 발령을 승인한다.
//  2. 상태가 CEO_PENDING_APPR(CEO 승인 대기)로 업데이트
//  3. CEO 또는 시스템 관리자가 승인한다.
//  4. 상태가 APPR(최종 승인) 상태로 변경되고 발령 구분에 따라 처리 로직을 수행한다.
    @Test
    @DisplayName("발령 승인")
    void approve() throws Exception {
        // 총무부 인사이동 발령 승인을 위해 총무부 부서장으로 로그인했다고 가정.
        UserDetails userDetails = userDetailsService.loadUserByUsername("LEADER2");
        userDetails.getAuthorities();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 조직장이 총무팀 조직 이동 발령 승인
        appointmentService.approve(23L, PageRequest.of(1, 10));

        Appointment appointment = appointmentRepository.findById(23L).get();
        assertThat(appointment.getApprovementStatus()).isEqualTo(ApprovementStatus.CEO_PENDING_APPR); // CEO 승인대기 상태로 변경.

        // 시스템 관리자 권한으로 로그인
        each();

        appointmentService.approve(23L, PageRequest.of(1, 10)); // 시스템 관리자 조직 이동 발령 승인

        Appointment appr = appointmentRepository.findById(23L).get();
        assertThat(appr.getApprovementStatus()).isEqualTo(ApprovementStatus.APPR); // 최종승인 상태로 변경.

        assertThat(appr.getTransOrg()).isEqualTo(appr.getEmployee().getOrganization()); // 최종 승인된 조직과 직원 엔터티의 조직이 같은지 확인.
        assertThat(appr.getTransOrg().getEmployees()).contains(appr.getEmployee());  // 조직 엔터티에 발령된 직원이 포함되어 있는지 확인.

        // 인사팀으로 새로운 조직 발령.
        AppointmentSaveDto dto = AppointmentSaveDto.builder()
                .startDate(LocalDate.of(2023, 1, 1))
                .orgNo("000030")
                .empNo("EMPLOYEE2")
                .type(AppointmentType.ORG)
                .build();

        AppointmentFindDto save = appointmentService.save(dto);

        System.out.println("save = " + save);

        UserDetails orgLeader = userDetailsService.loadUserByUsername("ORG_LEADER");
        orgLeader.getAuthorities();
        Authentication auth = new UsernamePasswordAuthenticationToken(orgLeader, "", orgLeader.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        appointmentService.approve(save.getAppointmentId(), PageRequest.of(1, 50));

        each(); // admin으로 로그인.

        Appointment latestApp = appointmentRepository.findLatestApp(appr.getEmployee().getId()).get();

        Page<AppointmentFindDto> approve = appointmentService.approve(save.getAppointmentId(), PageRequest.of(1, 50));

        entityManager.flush();
        entityManager.clear();

        Appointment appr2 = appointmentRepository.findById(save.getAppointmentId()).get();

        assertThat(latestApp.getEndDate()).isEqualTo(appr2.getStartDate().minusDays(1)); // 이전 조직발령 종료일 = 현 조직 발령 시작일 - 1일
    }
}
