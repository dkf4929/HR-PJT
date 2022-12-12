package project.hrpjt.organization.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import project.hrpjt.organization.dto.*;
import project.hrpjt.organization.entity.Organization;
import project.hrpjt.organization.entity.QOrganization;

import javax.persistence.EntityManager;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static project.hrpjt.employee.entity.QEmployee.*;
import static project.hrpjt.organization.entity.QOrganization.*;
import static project.hrpjt.organization.entity.QOrganization.organization;

public class OrganizationRepositoryCustomImpl implements OrganizationRepositoryCustom {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    @Autowired
    public OrganizationRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<OrganizationFindDto> findAllOrg(OrganizationFindParamDto dto) {
        QOrganization parent = new QOrganization("parent");

        List<Tuple> fetch = queryFactory
                .select(organization,
                        organization.parent
                )
                .distinct()
//                .from(organization).leftJoin(organization.parent, parent).fetchJoin()
                .from(organization)//.leftJoin(organization.parent, parent).fetchJoin()
                .orderBy(organization.orgNo.asc().nullsFirst())
                .fetch();

        Map<Organization, List<Tuple>> collect = fetch.stream().distinct()
                .collect(Collectors.groupingBy(tuple -> tuple.get(organization.parent)));

//        List<OrganizationFindDto> list = new ArrayList<>();
//
//
//        for (int i = 0; i < collect.keySet().size(); i++) {
//            OrganizationFindDto build = OrganizationFindDto.builder()
//                    .childs(collect.get(collect.keySet().iterator().next()).stream()
//                            .map(c -> c.get(organization)).collect(Collectors.toSet())
//                            .stream().collect(Collectors.groupingBy(c -> c))
//                            .values().stream().map(s -> s.iterator().next()).collect(Collectors.toSet()))
//                    .organization(collect.keySet().iterator().next())
//                    .build();
//
//            build.setParent(build);
//
//            list.add(build);
//        }
//
//        return list;
        return collect.keySet().stream()
                .distinct()
                .map(entry -> OrganizationFindDto.builder()
                        .childs(collect.get(entry).stream()
                                .map(c -> c.get(organization)).collect(Collectors.toSet())
                                .stream().collect(Collectors.groupingBy(c -> c))
                                .values().stream().map(s -> s.iterator().next()).collect(Collectors.toSet()))
                        .organization(entry.getOrganization())
                        .build())
                .collect(Collectors.toList());
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