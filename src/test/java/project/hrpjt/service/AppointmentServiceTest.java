package project.hrpjt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import org.assertj.core.api.Assertions;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.appointment.dto.AppointmentFindDto;
import project.hrpjt.appointment.dto.AppointmentSaveDto;
import project.hrpjt.appointment.entity.Appointment;
import project.hrpjt.appointment.entity.enumeration.AppointmentStatus;
import project.hrpjt.appointment.entity.enumeration.AppointmentType;
import project.hrpjt.appointment.repository.AppointmentRepository;
import project.hrpjt.appointment.service.AppointmentService;

import javax.servlet.http.Cookie;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AppointmentServiceTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AppointmentService appointmentService;
    @Autowired AppointmentRepository appointmentRepository;
    @Autowired UserDetailsService userDetailsService;

    AtomicReference<String> cookieValue = new AtomicReference<>("");

    @BeforeEach
    void each() throws Exception {
        mockMvc.perform(post("/login")
                        .param("empNo", "ADMIN")  // admin 권한으로 로그인
                        .param("password", "1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(result -> Arrays.stream(result.getResponse().getCookies())
                        .filter((c) -> c.getName().equals("jwtToken"))
                        .forEach((c) -> cookieValue.set(c.getValue())));   // result에서 쿠키값 추출
    }

    @Test
    @DisplayName("내 발령 이력 조회")
    void myAppointment() throws Exception {
        mockMvc.perform(get("/role_emp/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwtToken", cookieValue.get())))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println("result = " + result.getResponse().getContentAsString(Charset.forName("UTF-8"))));
    }

    @Test
    @DisplayName("발령 history 조회")
    void appointmentList() throws Exception {
        mockMvc.perform(get("/role_lead/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwtToken", cookieValue.get())))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println("result = " + result.getResponse().getContentAsString(Charset.forName("UTF-8"))));
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

        mockMvc.perform(post("/login")
                        .param("empNo", "ORG_LEADER")  // 인사부 조직장으로 로그인.
                        .param("password", "1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(result -> Arrays.stream(result.getResponse().getCookies())
                        .filter((c) -> c.getName().equals("jwtToken"))
                        .forEach((c) -> cookieValue.set(c.getValue())));

        mockMvc.perform(get("/role_lead/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwtToken", cookieValue.get())))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String contents = result.getResponse().getContentAsString(Charset.forName("UTF-8"));  // 결과를 string으로 받아온다.
                    JSONParser jsonParser = new JSONParser();

                    JSONObject obj = (JSONObject) jsonParser.parse(contents);  // json 파싱
                    String content = String.valueOf(obj.get("content"));

                    objectMapper = objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

                    Object[] objects = objectMapper.readValue(content, Object[].class);
                    List<AppointmentFindDto> list = objectMapper.convertValue(objects, new TypeReference<List<AppointmentFindDto>>() {});  // api 결과를 dto로 변환

                    assertThat(list).contains(savedDto);  // 등록한 발령이 리스트에 있는지 확인.
                });
    }

//  1. 조직장이 조직장 하위 조직 발령 또는 부서원의 발령을 승인한다.
//  2. 상태가 CEO_PENDING_APPR(CEO 승인 대기)로 업데이트
//  3. CEO 또는 시스템 관리자가 승인한다.
//  4. 상태가 APPR(최종 승인) 상태로 변경되고 발령 구분에 따라 처리 로직을 수행한다.
    @Test
    @DisplayName("발령 승인")
    void approve() throws Exception {
        mockMvc.perform(post("/login")
                        .param("empNo", "LEADER2")  // 총무부 인사이동 발령 승인을 위해 총무부 부서장으로 로그인.
                        .param("password", "1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(result -> Arrays.stream(result.getResponse().getCookies())
                        .filter((c) -> c.getName().equals("jwtToken"))
                        .forEach((c) -> cookieValue.set(c.getValue())));   // result에서 쿠키값 추출

        mockMvc.perform(put("/role_lead/approve/23")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwtToken", cookieValue.get())))
                .andExpect(status().isOk())
                .andDo(result -> {
                    Appointment appointment = appointmentRepository.findById(23L).get();
                    assertThat(appointment.getAppointmentStatus()).isEqualTo(AppointmentStatus.CEO_PENDING_APPR);
                });

        mockMvc.perform(post("/login")
                        .param("empNo", "CEO")  // CEO로 로그인.
                        .param("password", "1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(result -> Arrays.stream(result.getResponse().getCookies())
                        .filter((c) -> c.getName().equals("jwtToken"))
                        .forEach((c) -> cookieValue.set(c.getValue())));   // result에서 쿠키값 추출

        mockMvc.perform(put("/role_lead/approve/23")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwtToken", cookieValue.get())))
                .andExpect(status().isOk())
                .andDo(result -> {
                    Appointment appointment = appointmentRepository.findById(23L).get();
                    assertThat(appointment.getAppointmentStatus()).isEqualTo(AppointmentStatus.APPR);
                });
    }
}
