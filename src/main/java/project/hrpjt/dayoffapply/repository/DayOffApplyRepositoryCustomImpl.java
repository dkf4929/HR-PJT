package project.hrpjt.dayoffapply.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import project.hrpjt.appointment.entity.enumeration.ApprovementStatus;
import project.hrpjt.dayoffapply.entity.DayOffApply;
import project.hrpjt.dayoffapply.entity.QDayOffApply;

import javax.persistence.EntityManager;
import java.util.List;

import static project.hrpjt.dayoffapply.entity.QDayOffApply.*;

public class DayOffApplyRepositoryCustomImpl implements DayOffApplyRepositoryCustom {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public DayOffApplyRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<DayOffApply> findByEmpId(Long empId) {
        return queryFactory
                .selectFrom(dayOffApply)
                .where(dayOffApply.employee.id.eq(empId))
                .fetch();
    }

    @Override
    public List<DayOffApply> findByOrgIds(List<Long> orgIds) {
        return queryFactory
                .select(dayOffApply)
                .from(dayOffApply)
                .leftJoin(dayOffApply.employee).fetchJoin()
                .where(dayOffApply.employee.organization.id.in(orgIds))
                .fetch();
    }

    @Override
    public List<DayOffApply> findApprListByOrgIds(List<Long> orgIds) {
        return queryFactory
                .select(dayOffApply)
                .from(dayOffApply)
                .leftJoin(dayOffApply.employee).fetchJoin()
                .where(dayOffApply.employee.organization.id.in(orgIds).and(dayOffApply.status.eq(ApprovementStatus.LEADER_PENDING_APPR)))
                .fetch();
    }

    @Override
    public List<DayOffApply> findApprListCEO() {
        return queryFactory
                .selectFrom(dayOffApply)
                .where(dayOffApply.status.eq(ApprovementStatus.CEO_PENDING_APPR))
                .fetch();
    }
}
