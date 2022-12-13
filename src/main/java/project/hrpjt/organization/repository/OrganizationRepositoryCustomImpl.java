package project.hrpjt.organization.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import project.hrpjt.organization.dto.*;
import project.hrpjt.organization.entity.Organization;

import javax.persistence.EntityManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static project.hrpjt.employee.entity.QEmployee.*;
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
//        String date1 = organization.startDate.toString();
//        String date2 = organization.endDate.toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        try {
            Date startDate = dateFormat.parse(date1);
            Date endDate = dateFormat.parse(date2);

            List<Organization> organizationList = queryFactory
                    .selectFrom(organization)
                    .leftJoin(organization.parent).fetchJoin()
                    .leftJoin(organization.children).fetchJoin()
                    .where(Expressions.currentTimestamp().between(startDate, endDate))
                    .orderBy(organization.orgNo.asc())
                    .fetch();

            List<OrganizationFindDto> dtoList = new ArrayList<>();
            Map<Long, OrganizationFindDto> map = new HashMap<>();

            organizationList.stream()
                    .forEach(o -> {
                        OrganizationFindDto organizationFindDto = new OrganizationFindDto(o);

                        if (o.getParent() != null) {
                            organizationFindDto.setParentId(o.getParent().getId());
                        }

                        System.out.println("organizationFindDto = " + organizationFindDto);

                        map.put(organizationFindDto.getId(), organizationFindDto);

                        if (o.getParent() != null) {
                            map.get(o.getParent().getId()).getChild().add(organizationFindDto);
                        } else {
                            dtoList.add(organizationFindDto);
                        }
                    });

            return dtoList;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<OrganizerFindDto> findOrganizerByParam() {
        List<Organization> list = queryFactory
                .selectFrom(organization)
                .distinct()
                .leftJoin(organization.parent).fetchJoin()
                .leftJoin(organization.employees, employee).fetchJoin()
//                .where(dto.getOrgNo() == null ? null : organization.orgNo.ne("000001"))
//                .orderBy(organization.parent.orgNo.asc().nullsFirst())
                .fetch();

        List<OrganizerFindDto> dtoList = new ArrayList<>();
        Map<Long, OrganizerFindDto> map = new HashMap<>();

        list.stream().forEach(o -> {
            OrganizerFindDto dto = new OrganizerFindDto(o);

            if (o.getParent() != null) {
                dto.setParentId(o.getParent().getId());
            }

            map.put(dto.getId(), dto);

            if (o.getParent() != null) {
                map.get(o.getParent().getId()).getChild().add(dto);
            } else {
                dtoList.add(dto);
            }
        });

        return dtoList;
    }

    private BooleanExpression orgNmContain(String orgNm) {
        return orgNm == null ? null : organization.orgNm.contains(orgNm);
    }

    private BooleanExpression orgNoEq(String orgNo) {
        return orgNo == null ? organization.orgNo.eq("000001") : organization.orgNo.eq(orgNo); // default 최상위 조직
    }

}