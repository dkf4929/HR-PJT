package project.hrpjt.service;

import org.assertj.core.api.Assertions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.organization.dto.*;
import project.hrpjt.organization.entity.Organization;
import project.hrpjt.organization.repository.OrganizationRepository;
import project.hrpjt.organization.service.OrganizationService;

import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
public class OrganizationServiceTest {
    @Autowired OrganizationService organizationService;
    @Autowired UserDetailsService userDetailsService;
    @Autowired OrganizationRepository organizationRepository;
    @Autowired EntityManager entityManager;

    @BeforeEach
    void each() throws Exception {
        UserDetails userDetails = userDetailsService.loadUserByUsername("ADMIN");
        userDetails.getAuthorities();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("조직도 조회")
    void findAll() throws Exception {
        List<OrganizationFindDto> list = organizationService.findAll(3L);
        Set<Organization> children = new HashSet<>();

        children.stream().forEach(c -> {
            assertThat(c.getParent().getId()).isEqualTo(3L);
        });
    }

    @Test
    @DisplayName("조직원 조회")
    void findOrganizer() throws Exception {
        Page<OrganizerFindDto> organizer = organizationService.findOrganizerByParam(PageRequest.of(1, 50));

        System.out.println("size() = " + organizer.getContent().size());
    }

    @Test
    @DisplayName("조직 폐쇄")
    void delete() throws Exception {
        OrganizationFindParamDto dto = OrganizationFindParamDto.builder()
                .orgId(8L)
                .build();

        organizationService.close(dto, PageRequest.of(1, 50));

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

        organizationService.edit(dto, PageRequest.of(1, 50));

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
