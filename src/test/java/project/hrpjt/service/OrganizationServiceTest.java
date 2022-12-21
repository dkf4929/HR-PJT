package project.hrpjt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.employee.dto.EmployeeUpdateDto;
import project.hrpjt.organization.dto.*;
import project.hrpjt.organization.entity.Organization;
import project.hrpjt.organization.repository.OrganizationRepository;
import project.hrpjt.organization.service.OrganizationService;

import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;
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
    @Autowired
    EntityManager entityManager;

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
    @DisplayName("조직도 조회")
    void findAll() throws Exception {
//        OrganizationFindParamDto dto = OrganizationFindParamDto.builder()
//                .orgId(3L)
//                .build();
//
//        String value = objectMapper.writeValueAsString(dto);
        mockMvc.perform(get("/role_emp/organization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("orgId", "3")
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

                    assertThat(orgNm).isEqualTo("총무부");
                });
    }

    @Test
    @DisplayName("조직원 조회")
    void findOrganizer() throws Exception {
        mockMvc.perform(get("/role_emp/organization/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwtToken", cookieValue.get())))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("조직 폐쇄")
    void delete() throws Exception {
        OrganizationFindParamDto dto = OrganizationFindParamDto.builder()
                .orgId(8L)
                .build();

        String value = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.delete("/role_adm/organization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(value)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwtToken", cookieValue.get())))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        Organization organization = organizationRepository.findById(8L).get();
        Set<Organization> children = organization.getChildren();

        assertThat(organization.getEndDate()).isEqualTo(LocalDate.now().minusDays(1));
        
        for (Organization child : children) {
            assertThat(child.getEndDate()).isEqualTo(LocalDate.now().minusDays(1));
        }
    }

    @Test
    @DisplayName("조직 수정")
    void update() throws Exception {
        List<Long> list = new ArrayList<>();

        OrganizationUpdateDto dto = OrganizationUpdateDto.builder()
                .updateOrgId(3L)
                .orgNo("123456")
                .orgNm("테스트")
                .parentId(2L)
                .build();

        String value = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put("/role_lead/organization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(value)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("jwtToken", cookieValue.get())))
                .andExpect(status().isOk());

        Organization findOrg = organizationRepository.findById(3L).get();
        Set<Organization> children = findOrg.getChildren();
        Organization parent = organizationRepository.findById(2L).get();

        assertThat(findOrg.getOrgNm()).isEqualTo("테스트");

        assertThat(findOrg.getOrgNo()).isEqualTo("123456");

        children.stream().forEach(c -> {
            assertThat(c.getParent()).isEqualTo(findOrg);
        });

        assertThat(findOrg.getParent()).isEqualTo(parent);
    }
}
