package project.hrpjt.holiday.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import project.hrpjt.holiday.entity.Holidays;
import project.hrpjt.holiday.entity.QHolidays;

import javax.persistence.EntityManager;
import java.util.List;

import static project.hrpjt.holiday.entity.QHolidays.*;

public class HolidayRepositoryCustomImpl implements HolidayRepositoryCustom {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public HolidayRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Holidays> findByYears(List<Integer> years) {
        return queryFactory
                .selectFrom(holidays)
                .where(holidays.year.in(years))
                .fetch();
    }
}
