package project.hrpjt.appointment.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import project.hrpjt.appointment.dto.AppointmentFindDto;
import project.hrpjt.appointment.entity.Appointment;
import project.hrpjt.appointment.entity.QAppointment;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.organization.dto.OrganizationFindDto;
import project.hrpjt.organization.repository.OrganizationRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static project.hrpjt.appointment.entity.QAppointment.appointment;

public class AppointmentRepositoryCustomImpl implements AppointmentRepositoryCustom {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    private final OrganizationRepository organizationRepository;

    public AppointmentRepositoryCustomImpl(EntityManager entityManager, OrganizationRepository organizationRepository) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.organizationRepository = organizationRepository;
    }

    @Override
    public List<AppointmentFindDto> findAllByParam(String empNo, String empNm) {
        Employee loginEmp = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Appointment> object = new ArrayList<>();

        if (loginEmp.getRole().equals("ROLE_ORG_LEADER")) {
            List<Long> orgIds = getIds(loginEmp);

            object = queryFactory
                    .select(appointment)
                    .from(appointment)
                    .join(appointment.employee).fetchJoin()
                    .leftJoin(appointment.transOrg).fetchJoin()
                    .where(empNoEq(empNo), empNmEq(empNm), authCheck(loginEmp, orgIds))
                    .fetch();
        } else {
            object = queryFactory
                    .select(appointment)
                    .from(appointment)
                    .join(appointment.employee).fetchJoin()
                    .leftJoin(appointment.transOrg).fetchJoin()
                    .where(empNoEq(empNo), empNmEq(empNm))
                    .fetch();
        }

        return objectToDto(object);
    }

    @Override
    public List<AppointmentFindDto> findMyAppointment(Long empId) {
        List<Appointment> resultList = entityManager.createQuery(
                        "select a" +
                                " from Appointment a" +
                                " join a.employee" +
                                " where a.employee.id = :empId"
                ).setParameter("empId", empId)
                .getResultList();

        return objectToDto(resultList);
    }

    @Override
    public List<Appointment> findAllLeave(Long empId) {
        return queryFactory
                    .select(appointment)
                    .from(appointment)
                    .join(appointment.employee)
                    .where(appointment.employee.id.eq(empId).and(appointment.appointmentType.stringValue().like("%LEAVE")))
                    .fetch();
    }

    @Override
    public Optional<Appointment> findByIdFetch(Long id) {
        QAppointment sub = new QAppointment("sub");
        return Optional.ofNullable(queryFactory
                .select(appointment)
                .distinct()
                .from(appointment)
                .leftJoin(appointment.transOrg).fetchJoin()
                .leftJoin(appointment.employee).fetchJoin()
                .where(appointment.id.eq(id)).fetchOne());
    }

    @Override
    public Optional<Appointment> findLatestApp(Long empId) {
        QAppointment sub = new QAppointment("sub");

        return Optional.ofNullable(queryFactory
                .select(appointment)
                .distinct()
                .from(appointment)
                .leftJoin(appointment.transOrg).fetchJoin()
                .leftJoin(appointment.employee).fetchJoin()
                .where(appointment.appointmentType.stringValue().eq("ORG")
                        .and(appointment.approvementStatus.stringValue().eq("APPR"))
                        .and(appointment.endDate.eq(LocalDate.of(2999, 12, 31)))
                        .and(appointment.startDate.eq(JPAExpressions
                                .select(sub.startDate.min())
                                .distinct()
                                .from(sub)
                                .where(sub.employee.id.eq(empId)
                                        .and(sub.approvementStatus.stringValue().eq("APPR"))
                                        .and(sub.appointmentType.stringValue().eq("ORG"))
                                        .and(sub.endDate.eq(LocalDate.of(2999, 12, 31))))))
                        .and(appointment.employee.id.eq(empId))
                )
                .fetchOne());
    }

    @Override
    public List<AppointmentFindDto> findAllApprList(Employee employee) {
        List<Appointment> object = new ArrayList<>();

        if (employee.getRole().equals("ROLE_ORG_LEADER")) {  // 조직장 승인 리스트
            List<Long> orgIds = getIds(employee);

            object = queryFactory
                    .select(appointment)
                    .from(appointment)
                    .join(appointment.employee).fetchJoin()
                    .leftJoin(appointment.transOrg).fetchJoin()
                    .where(authCheck(employee, orgIds).and(appointment.approvementStatus.stringValue().eq("LEADER_PENDING_APPR")))
                    .fetch();
        } else {  // 시스템 관리자 / ceo 승인 리스트
            object = queryFactory
                    .select(appointment)
                    .from(appointment)
                    .join(appointment.employee).fetchJoin()
                    .leftJoin(appointment.transOrg).fetchJoin()
                    .where(appointment.approvementStatus.stringValue().eq("CEO_PENDING_APPR"))
                    .fetch();
        }

        return objectToDto(object);
    }

    @Override
    public Long findOrgCount(Long id) {
        return queryFactory
                .select(appointment.id.count())
                .from(appointment)
                .where(appointment.employee.id.eq(id).and(appointment.appointmentType.stringValue().eq("ORG")))
                .fetchOne();
    }

    private BooleanExpression authCheck(Employee loginEmp, List<Long> orgIds) {
        if (loginEmp.getRole().equals("ROLE_ORG_LEADER")) {
            return appointment.transOrg.id.in(orgIds).or(appointment.employee.organization.id.in(orgIds).and(appointment.transOrg.isNull())); // 조직장의 조직으로 조직 이동하는 발령이거나 하위 조직원 발령
        } else {
            return null;
        }
    }

    private BooleanExpression empNoEq(String empNo) {
        return empNo == null ? null : appointment.employee.empNo.eq(empNo);
    }

    private BooleanExpression empNmEq(String empNm) {
        return empNm == null ? null : appointment.employee.empNm.eq(empNm);
    }

    private List<AppointmentFindDto> objectToDto(List<Appointment> object) {
        return object.stream()
                .map(o -> {
                    return AppointmentFindDto.builder()
                            .appointmentType(o.getAppointmentType())
                            .startDate(o.getStartDate())
                            .transOrg(o.getTransOrg())
                            .approvementStatus(o.getApprovementStatus())
                            .endDate(o.getEndDate())
                            .empNm(o.getEmployee().getEmpNm())
                            .empNo(o.getEmployee().getEmpNo())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<Long> getIds(Employee loginEmp) {
        List<OrganizationFindDto> allOrg = organizationRepository.findAllOrg(loginEmp.getOrganization().getId());  // 조직장의 하위 조직 정보 추출

        List<Long> orgIds = new ArrayList<>();

        allOrg.stream().forEach(o -> {
            orgIds.add(o.getId());

            o.getChild().stream().forEach(c -> {
                orgIds.add(c.getId());
            });
        });
        return orgIds;
    }

}