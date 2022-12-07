package project.hrpjt.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.Cookie;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.employee.dto.EmployeeFindDto;
import project.hrpjt.employee.dto.EmployeeUpdateDto;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.employee.repository.EmployeeRepository;
import project.hrpjt.employee.service.EmployeeService;
import project.hrpjt.exception.NoSuchEmployeeException;
import project.hrpjt.organization.repository.OrganizationRepository;
import project.hrpjt.organization.service.OrganizationService;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class EmployeeServiceTest {
    @Autowired EmployeeService employeeService;
    @Autowired
    OrganizationRepository organizationRepository;
    @Autowired OrganizationService organizationService;
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired EmployeeRepository employeeRepository;


    AtomicReference<String> cookieValue = new AtomicReference<>("");

    @BeforeEach
    void each() throws Exception {
        mockMvc.perform(post("/login")
                        .param("empNo", "ADMIN")  // admin 권한으로 로그인
                        .param("password", "1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> Arrays.stream(result.getResponse().getCookies())
                        .filter((c) -> c.getName().equals("jwtToken"))
                        .forEach((c) -> cookieValue.set(c.getValue())));   // result에서 쿠키값 추출
    }

    @Test
    @DisplayName("유저 권한으로 사원 생성 시 redirect")
    void userAuthTest() throws Exception {
        mockMvc.perform(post("/login")
                        .param("empNo", "USER")  // user 권한으로 로그인
                        .param("password", "1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> Arrays.stream(result.getResponse().getCookies())
                        .filter((c) -> c.getName().equals("jwtToken"))
                        .forEach((c) -> cookieValue.set(c.getValue())));   // result에서 쿠키값 추출

        mockMvc.perform(post("/role_adm/employees/add")
                        .param("empNo", "userA")
                        .param("empNm", "userA")
                        .param("gender", "M")
                        .param("role", "ROLE_EMPLOYEE")
                        .param("organizationId", "2")
                        .param("password", "1234")
                        .cookie(new Cookie("jwtToken", cookieValue.get()))  // set cookie
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // admin 권한일 경우 ok
                .andDo(print());
    }

    @Test
    @DisplayName("시스템 권한으로 직원 생성")
    void adminAuthTest() throws Exception {
        mockMvc.perform(post("/role_adm/employees/add")
                        .param("empNo", "userA")
                        .param("empNm", "userA")
                        .param("gender", "M")
                        .param("role", "ROLE_EMPLOYEE")
                        .param("organizationId", "2")
                        .param("password", "1234")
                        .cookie(new Cookie("jwtToken", cookieValue.get()))  // set cookie
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // admin 권한일 경우 ok
                .andDo(print());
    }

    @Test
    @DisplayName("모든 직원 검색")
    void findAll() throws Exception {
        PageRequest pageRequest = PageRequest.of(1, 10);
        Page<EmployeeFindDto> all = employeeService.findAll(pageRequest);

        assertThat(all.getSize()).isEqualTo(3);
    }

    @Test
    @DisplayName("직원 삭제")
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/role_adm/employees/delete")
                        .param("employeeId", "5")
                        .cookie(new Cookie("jwtToken", cookieValue.get()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        assertThatThrownBy(() -> {
            employeeService.findEmployee(2L);
        }).isInstanceOf(NoSuchEmployeeException.class);
    }

    @Test
    @DisplayName("직원 정보 수정")
    void update() throws Exception {
        EmployeeUpdateDto dto = EmployeeUpdateDto.builder()
                .employeeId(5L)
                .organizationId(1L)
                .build();

        String value = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put("/role_emp/employees/edit")
                        .content(value)
                        .cookie(new Cookie("jwtToken", cookieValue.get()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        Employee employee = employeeRepository.findById(5L).get();

        Assertions.assertThat(employee.getOrganization().getOrgNm()).isEqualTo("company");

    }
}
