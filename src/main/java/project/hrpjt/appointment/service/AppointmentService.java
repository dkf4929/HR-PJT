package project.hrpjt.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.appointment.dto.AppointmentFindDto;
import project.hrpjt.appointment.dto.AppointmentSaveDto;
import project.hrpjt.appointment.entity.Appointment;
import project.hrpjt.appointment.entity.enumeration.AppointmentStatus;
import project.hrpjt.appointment.entity.enumeration.AppointmentType;
import project.hrpjt.appointment.repository.AppointmentRepository;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.employee.repository.EmployeeRepository;
import project.hrpjt.organization.entity.Organization;
import project.hrpjt.organization.repository.OrganizationRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final OrganizationRepository organizationRepository;
    private final EmployeeRepository employeeRepository;
    private final EntityManager entityManager;

    public AppointmentFindDto save(AppointmentSaveDto param) {
        Organization transOrg = null;

        if (param.getOrgNo() != null) {
            transOrg = organizationRepository.findByOrgNo(param.getOrgNo()).orElseThrow();
        }
        Employee employee = employeeRepository.findByEmpNo(param.getEmpNo()).orElseThrow();

        Appointment save = appointmentRepository.save(Appointment.builder()
                .appointmentType(param.getType())
                .transOrg(transOrg)
                .appointmentStatus(param.getStatus())
                .employee(employee)
                .startDate(param.getStartDate())
                .endDate(param.getEndDate())
                .build());

        entityManager.flush();
        entityManager.clear();

        Appointment object = appointmentRepository.findById(save.getId()).orElseThrow();

        return getBuild(object);
    }

    public Page<AppointmentFindDto> findAllByParam(String empNo, String empNm, Pageable pageable) {
        List<AppointmentFindDto> list = appointmentRepository.findAllByParam(empNo, empNm);
        return new PageImpl<>(list, pageable, list.size());
    }

    public Page<AppointmentFindDto> findMyAppointment(Pageable pageable) {
        Employee loginEmp = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<AppointmentFindDto> app = appointmentRepository.findMyAppointment(loginEmp.getId());

        return new PageImpl<>(app, pageable, app.size());
    }

    public Page<AppointmentFindDto> findAllApprList(Pageable pageable) {
        Employee loginEmp = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<AppointmentFindDto> list = appointmentRepository.findAllApprList(loginEmp);

        return new PageImpl<>(list, pageable, list.size());
    }

    public Page<AppointmentFindDto> approve(Long appId, Pageable pageable) {
        Employee loginEmp = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = loginEmp.getRole();

        Appointment appointment = getAppointment(appId, role);

        if (pageable != null) { // testdata -> pageable null
            return findAllByParam(appointment.getEmployee().getEmpNo(), appointment.getEmployee().getEmpNm(), pageable);
        } else {
            return null;
        }
    }

    private AppointmentFindDto getBuild(Appointment object) {
        return AppointmentFindDto.builder()
                .appointmentStatus(object.getAppointmentStatus())
                .appointmentType(object.getAppointmentType())
                .empNm(object.getEmployee().getEmpNm())
                .empNo(object.getEmployee().getEmpNo())
                .endDate(object.getEndDate())
                .transOrg(object.getTransOrg())
                .startDate(object.getStartDate())
                .build();
    }

    private Appointment getAppointment(Long appId, String role) {
        Appointment appointment = appointmentRepository.findByIdFetch(appId).orElseThrow();  // 발령 정보 추출

        if (role.equals("ROLE_ORG_LEADER")) {
            appointment.updateAppointmentStatus(AppointmentStatus.CEO_PENDING_APPR); // 조직장 승인 (CEO 승인 대기) 상태로 변경
        } else {
            if (appointment.getTransOrg() != null && (role.equals("ROLE_SYS_ADMIN")) || role.equals("ROLE_CEO")) {  // 조직 이동 발령이면 직전 조직 발령 찾아서 endDate를 업데이트.
                appointment.updateAppointmentStatus(AppointmentStatus.APPR);
                Employee employee = appointment.getEmployee();

                if (appointmentRepository.findOrgCount(appointment.getEmployee().getId()) > 1) {  // 이전 조직 발령이 존재할 경우
                    Optional<Appointment> latestAppointment = appointmentRepository.findLatestApp(employee.getId());// 가장 최근의 조직발령을 찾아온다.

                    if (!latestAppointment.isEmpty()) {
                        latestAppointment.get().getTransOrg().getEmployees().remove(latestAppointment.get().getEmployee());  // 조직에서 발령대상자 삭제.
                        latestAppointment.get().updateEndDate(appointment.getStartDate().minusDays(1)); // 새로운 조직발령 시작일 -1일
                    }
                }

                appointment.getTransOrg().getEmployees().add(appointment.getEmployee());   // 조직에 해당 발령대상자를 추가
                employee.updateOrganization(appointment.getTransOrg());  // 조직 업데이트
            } else {
                appointment.updateAppointmentStatus(AppointmentStatus.APPR); // 승인 처리

                /* 퇴사 시 처리 - 이전 조직발령의 종료일을 오늘날짜 -1일
                 * 직원 엔터티의 조직 null로 업데이트.
                 * 조직 엔터티에서 직원 삭제
                 */
                if (appointment.getAppointmentType().equals(AppointmentType.RETIRE)) {
                    // 퇴직일자를 현재일자로 업데이트
                    Employee employee = appointment.getEmployee();

                    employee.updatRetireDate(LocalDate.now());
                    Optional<Appointment> latestAppointment = appointmentRepository.findLatestApp(appointment.getEmployee().getId()); // 조직 발령을 가져온다.

                    if (!latestAppointment.isEmpty()) {
                        latestAppointment.get().updateEndDate(LocalDate.now().minusDays(1)); // 새로운 조직발령 시작일 -1일
                    }
                    // 조직 삭제
                    employee.updateOrganization(null);
                    latestAppointment.get().getTransOrg().getEmployees().remove(employee);
                }
            }
        }
        return appointment;
    }
}
