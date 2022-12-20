package project.hrpjt.organization.repository;

import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import project.hrpjt.employee.entity.Employee;
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
    public List<OrganizationFindDto> findAllOrg(Long orgId) {
//            List<Organization> organizationList = entityManager.createQuery("select o from Organization o" +
//                            " left join fetch o.parent p" +
//                            " where :current between o.startDate and o.endDate" +
//                            " order by o.orgNo asc", Organization.class).setParameter("current", LocalDate.now())
//                    .getResultList();

        List<Organization> organizationList = entityManager.createNativeQuery(
                getSqlString(), Organization.class).setParameter("org_id", orgId).getResultList();

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
    public List<OrganizerFindDto> findOrganizerByParam(OrganizerFindParamDto param) {
        List<Organization> list = entityManager.createNativeQuery(
                getSqlString(), Organization.class).setParameter("org_id", param.getOrgId()).getResultList();

        List<OrganizerFindDto> dtoList = new ArrayList<>();
        Map<Long, OrganizerFindDto> map = new HashMap<>();

        list.forEach(o -> {
            OrganizerFindDto dto = new OrganizerFindDto(o);

            if (o.getParent() != null) {
                dto.setParentId(o.getParent().getId());
            }

            map.put(dto.getId(), dto);


            if (o.getParent() != null  && map.size() > 1) { // 첫번째 요소인지 판별
                map.get(o.getParent().getId()).getChild().add(dto);
            } else {
                dtoList.add(dto);
            }
        });

        return dtoList;
    }

    @Override
    public Optional<Organization> findOrgById(Long id) {
        return Optional.ofNullable(entityManager.createQuery(
                        "select o " +
                                "from Organization o" +
                                " where o.id = :org_id" +
                                " and (:current between o.startDate and o.endDate)", Organization.class
                ).setParameter("current", LocalDate.now())
                .setParameter("org_id", id)
                .getSingleResult());
    }

    @Override
    public List<Organization> findAllByOrgNo(List<String> orgNoList) {
        return queryFactory
                .selectFrom(organization)
                .where(organization.orgNo.in(orgNoList))
                .fetch();
    }

    @Override
    public Optional<Organization> findByOrgNo(String orgNo) {
        return Optional.ofNullable(queryFactory
                .select(organization)
                .from(organization)
                .where(organization.orgNo.eq(orgNo))
                .fetchOne());
    }

    @Override
    public List<Organization> findAllChild(Long orgId) {
        List<Organization> organizationList = entityManager.createNativeQuery(
                getSqlString(), Organization.class).setParameter("org_id", orgId).getResultList();

        return organizationList;
    }

    @Override //업데이트 쿼리 id 수만큼 나가서 jpql로 수정함.
    public int updateEndDate(List<Organization> child) {
        return entityManager.createQuery("update Organization o set o.endDate = :end_date where o in (:childs)")
                .setParameter("end_date", LocalDate.now().minusDays(1))
                .setParameter("childs", child)
                .executeUpdate();
    }

    @Override
    public Optional<Organization> findOrgByEmp(Employee employee) {
        return Optional.ofNullable(queryFactory
                .selectFrom(organization)
                .where(organization.employees.contains(employee))
                .fetchOne());
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