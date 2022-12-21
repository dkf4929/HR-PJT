package project.hrpjt.employee.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.support.QOracle10gDialect;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.employee.entity.QEmployee;
import project.hrpjt.organization.entity.Organization;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static project.hrpjt.employee.entity.QEmployee.*;

public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public EmployeeRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Long countInOfficeEmp(Organization organization) {
        return queryFactory
                .query()
                .select(employee.count())
                .from(employee)
                .where(employee.organization.eq(organization).and(employee.retireDate.isNull()))
                .fetchOne();
    }

    @Override
    public List<Employee> findAllFetch() {
        return queryFactory
                .select(employee)
                .from(employee)
                .leftJoin(employee.organization).fetchJoin()
                .fetch();
    }

    @Override
    public Optional<Employee> findByIdFetch(Long empId) {
        return Optional.ofNullable(
                queryFactory
                    .select(employee)
                    .from(employee)
                    .leftJoin(employee.organization).fetchJoin().where(employee.id.eq(empId))
                    .fetchOne());
    }
}
