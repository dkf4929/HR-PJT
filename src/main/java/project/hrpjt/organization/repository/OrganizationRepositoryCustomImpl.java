package project.hrpjt.organization.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import project.hrpjt.organization.dto.OrganizationFindDto;
import project.hrpjt.organization.dto.OrganizationFindParamDto;
import project.hrpjt.organization.dto.OrganizerFindDto;
import project.hrpjt.organization.dto.OrganizerFindParamDto;
import project.hrpjt.organization.entity.Organization;
import project.hrpjt.organization.entity.QOrganization;

import javax.persistence.EntityManager;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import static project.hrpjt.employee.entity.QEmployee.*;
import static project.hrpjt.organization.entity.QOrganization.*;

public class OrganizationRepositoryCustomImpl implements OrganizationRepositoryCustom {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    @Autowired
    public OrganizationRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Organization> findAllOrg(OrganizationFindParamDto dto) {
        QOrganization parent = new QOrganization("parent");
        QOrganization children = new QOrganization("children");  // 패치조인을 위한 객체 생성.

        return queryFactory
                .select(organization)
                .distinct()
                .from(organization).leftJoin(organization.parent, parent).fetchJoin()
                .from(organization).leftJoin(organization.children, children).fetchJoin()
                .where(orgNmContain(dto.getOrgNm()), orgNoEq(dto.getOrgNo()))
                .fetch();
    }

    @Override
    public List<OrganizerFindDto> findOrganizerByParam(OrganizerFindParamDto dto) {
        QOrganization children = new QOrganization("children");

        List<Tuple> fetch = queryFactory
                .select(organization, organization.employees, organization.children)
                .distinct()
                .from(organization).join(organization.employees, employee).fetchJoin()
                .leftJoin(organization.children, children).fetchJoin()
                .where(orgNmContain(dto.getOrgNm()), orgNoEq(dto.getOrgNo()))
                .fetch();

        return fetch.stream()
                .map(f -> OrganizerFindDto.builder()
                        .organization(f.get(organization))
                        .build())
                .collect(Collectors.toList());
    }

    private BooleanExpression orgNmContain(String orgNm) {
        return orgNm == null ? null : organization.orgNm.contains(orgNm);
    }

    private BooleanExpression orgNoEq(String orgNo) {
        return orgNo == null ? organization.orgNo.eq("000001") : organization.orgNo.eq(orgNo); // default 최상위 조직
    }

}
