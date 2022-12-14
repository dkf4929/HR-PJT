package project.hrpjt.organization.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
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
//            List<Organization> organizationList = entityManager.createQuery("select o from Organization o" +
//                            " left join fetch o.parent p" +
//                            " where :current between o.startDate and o.endDate" +
//                            " order by o.orgNo asc", Organization.class).setParameter("current", LocalDate.now())
//                    .getResultList();

        List<Organization> organizationList = entityManager.createNativeQuery(
                getSqlString(), Organization.class).setParameter("org_id", dto.getOrgId()).getResultList();

        List<OrganizationFindDto> dtoList = new ArrayList<>();
        Map<Long, OrganizationFindDto> map = new HashMap<>();

        organizationList.stream()
                .forEach(o -> {
                    OrganizationFindDto organizationFindDto = new OrganizationFindDto(o);

                    if (o.getParent() != null) {
                        organizationFindDto.setParentId(o.getParent().getId());
                    }

                    map.put(organizationFindDto.getId(), organizationFindDto);


                    if (o.getParent() != null  && map.size() > 1) { // 첫번째 요소인지 판별
                        map.get(o.getParent().getId()).getChild().add(organizationFindDto);
                    } else {
                        dtoList.add(organizationFindDto);
                    }
                });

        return dtoList;
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

    @Override
    public List<Organization> findAllChild(Long orgId) {
        return entityManager.createNativeQuery(
                getSqlString(), Organization.class).setParameter("org_id", orgId).getResultList();
    }

    private String getSqlString() {
        return "with recursive cte (org_id, org_nm, parent_id, created_date, last_modified_date, last_modified_by, created_by, start_date, end_date, org_no) as (" +
                "  select     org_id," +
                "              org_nm," +
                "             parent_id," +
                "             created_date," +
                "             last_modified_date," +
                "             last_modified_by," +
                "             created_by," +
                "             start_date," +
                "             end_date," +
                "             org_no" +
                "  from       organization" +
                "  where      parent_id = case when :org_id is null then 1 else :org_id end" +
                "  and        sysdate() between start_date and end_date" +
                "  union all" +
                "  select     d.org_id," +
                "              d.org_nm," +
                "             d.parent_id," +
                "             d.created_date," +
                "             d.last_modified_date," +
                "             d.last_modified_by," +
                "             d.created_by," +
                "             d.start_date," +
                "             d.end_date," +
                "             d.org_no" +
                "  from       organization d" +
                "  inner join cte" +
                "          on d.parent_id= cte.org_id" +
                ")" +
                "  select     org_id," +
                "              org_nm," +
                "             parent_id," +
                "             created_date," +
                "             last_modified_date," +
                "             last_modified_by," +
                "             created_by," +
                "             start_date," +
                "             end_date," +
                "             org_no" +
                "  from       organization" +
                "  where      org_id = case when :org_id is null then 1 else :org_id end" +
                " union all" +
                " select * from cte";
    }

    private BooleanExpression orgNmContain(String orgNm) {
        return orgNm == null ? null : organization.orgNm.contains(orgNm);
    }

    private BooleanExpression orgNoEq(String orgNo) {
        return orgNo == null ? organization.orgNo.eq("000001") : organization.orgNo.eq(orgNo); // default 최상위 조직
    }

}