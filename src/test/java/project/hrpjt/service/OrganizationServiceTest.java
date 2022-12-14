package project.hrpjt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.employee.dto.EmployeeUpdateDto;
import project.hrpjt.organization.dto.OrganizationFindParamDto;
import project.hrpjt.organization.dto.OrganizationSaveDto;
import project.hrpjt.organization.dto.OrganizerFindParamDto;
import project.hrpjt.organization.entity.Organization;
import project.hrpjt.organization.repository.OrganizationRepository;
import project.hrpjt.organization.service.OrganizationService;

import javax.servlet.http.Cookie;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrganizationServiceTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    OrganizationService organizationService;
    @Autowired
    OrganizationRepository organizationRepository;
    @Autowired
    ObjectMapper objectMapper;

    AtomicReference<String> cookieValue = new AtomicReference<>("");

    @BeforeEach
    void each() throws Exception {
        System.out.println("-------------------each-------------------");
        mockMvc.perform(post("/login")
                        .param("empNo", "ADMIN")  // admin 권한으로 로그인
                        .param("password", "1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> Arrays.stream(result.getResponse().getCookies())
                        .filter((c) -> c.getName().equals("jwtToken"))
                        .forEach((c) -> cookieValue.set(c.getValue())));   // result에서 쿠키값 추출
        System.out.println("-------------------each-------------------");
    }

    @Test
    @Commit
    void save() {
        List<Long> ids = new ArrayList<>();

        ids.add(6L);
        ids.add(7L);

        OrganizationSaveDto hbm = OrganizationSaveDto.builder()
                .orgNm("복리후생")
                .parentOrgId(3L)
                .orgNo("000021")
                .childOrgId(ids)
                .startDate(LocalDate.now())
                .build();

        Organization hbmOrg = organizationService.save(hbm);
        Organization organization1 = organizationRepository.findById(6L).get();
        Organization organization2 = organizationRepository.findById(7L).get();

        Assertions.assertThat(organization1.getParent()).isEqualTo(hbmOrg);
        Assertions.assertThat(organization2.getParent()).isEqualTo(hbmOrg);
    }

    @Test
    @DisplayName("조직도 조회")
    void findAll() throws Exception {
        OrganizationFindParamDto dto = OrganizationFindParamDto.builder()
                .orgNm("총무부")
                .build();

        String value = objectMapper.writeValueAsString(dto);

        mockMvc.perform(get("/role_emp/organization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(value)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwtToken", cookieValue.get())))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(result.getResponse().getContentAsString(Charset.forName("UTF-8")));
                    JSONArray array = (JSONArray) jsonObject.get("content");

                    String orgNm = "";

                    for (int i = 0; i < array.size(); i++) {
                        JSONObject obj = (JSONObject) array.get(i);
                        orgNm = (String) obj.get("orgNm");
                    }

                    Assertions.assertThat(orgNm).isEqualTo("총무부");
                });
    }

    @Test
    @DisplayName("조직원 조회")
    void findOrganizer() throws Exception {
        OrganizerFindParamDto dto = new OrganizerFindParamDto("000001", null);

        String value = objectMapper.writeValueAsString(dto);

        mockMvc.perform(get("/role_emp/organization/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(value)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwtToken", cookieValue.get())))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("권한 없는 조직 조회")
    void findOrganizerNoAuth() throws Exception {
        mockMvc.perform(post("/login")
                        .param("empNo", "ORG_LEADER")
                        .param("password", "1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> Arrays.stream(result.getResponse().getCookies())
                        .filter((c) -> c.getName().equals("jwtToken"))
                        .forEach((c) -> cookieValue.set(c.getValue())));

        OrganizerFindParamDto dto = new OrganizerFindParamDto("000001", null);

        String value = objectMapper.writeValueAsString(dto);

        mockMvc.perform(get("/role_emp/organization/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(value)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwtToken", cookieValue.get())))
                .andExpect(status().is3xxRedirection()); // 미인증 시 redirect
    }

    @Test
    @DisplayName("조직 삭제")
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/role_adm/organization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("orgId", "4")
                        .param("page", "1")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwtToken", cookieValue.get())))
                .andExpect(status().is5xxServerError());
    }
}
