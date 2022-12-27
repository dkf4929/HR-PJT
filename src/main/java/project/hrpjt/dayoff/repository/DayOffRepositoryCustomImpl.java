package project.hrpjt.dayoff.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import project.hrpjt.dayoff.entity.DayOff;
import project.hrpjt.dayoff.entity.QDayOff;
import project.hrpjt.employee.entity.Employee;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static project.hrpjt.dayoff.entity.QDayOff.*;

public class DayOffRepositoryCustomImpl implements DayOffRepositoryCustom {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public DayOffRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<DayOff> findMyDayOff(Employee employee, List<Integer> years) {
        return queryFactory
                .selectFrom(dayOff)
                .where(dayOff.employee.eq(employee).and(dayOff.year.in(years)))
                .orderBy(dayOff.year.asc())
                .fetch();
    }

    @Override
    public Optional<DayOff> findDayOff(Employee employee, int year) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(dayOff)
                        .where(dayOff.employee.eq(employee).and(dayOff.year.eq(year)))
                        .fetchOne()
        );
    }

    @Override
    public List<DayOff> findUseTarget() {
        return queryFactory
                .select(dayOff)
                .from(dayOff)
                .join(dayOff.employee).fetchJoin()
                .where(dayOff.annualDayOff.goe(10).and(dayOff.year.eq(LocalDate.now().getYear())))
                .fetch();
    }
}
