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
import project.hrpjt.appointment.entity.enumeration.ApprovementStatus;
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
                .approvementStatus(param.getStatus())
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
                .appointmentId(object.getId())
                .approvementStatus(object.getApprovementStatus())
                .appointmentType(object.getAppointmentType())
                .empNm(object.getEmployee().getEmpNm())
                .empNo(object.getEmployee().getEmpNo())
                .endDate(object.getEndDate())
                .transOrg(object.getTransOrg())
                .startDate(object.getStartDate())
                .build();
    }

    private Appointment getAppointment(Long appId, String role) {
        Appointment appointment = appointmentRepository.findByIdFetch(appId).orElseThrow();  // ?????? ?????? ??????

        if (role.equals("ROLE_ORG_LEADER")) {
            appointment.updateApprovementStatus(ApprovementStatus.CEO_PENDING_APPR); // ????????? ?????? (CEO ?????? ??????) ????????? ??????
        } else {
            if (appointment.getTransOrg() != null && (role.equals("ROLE_SYS_ADMIN")) || role.equals("ROLE_CEO")) {  // ?????? ?????? ???????????? ?????? ?????? ?????? ????????? endDate??? ????????????.
                appointment.updateApprovementStatus(ApprovementStatus.APPR);
                Employee employee = appointment.getEmployee();

                if (appointmentRepository.findOrgCount(appointment.getEmployee().getId()) > 1) {  // ?????? ?????? ????????? ????????? ??????
                    Optional<Appointment> latestAppointment = appointmentRepository.findLatestApp(employee.getId());// ?????? ????????? ??????????????? ????????????.

                    if (!latestAppointment.isEmpty()) {
                        latestAppointment.get().getTransOrg().getEmployees().remove(latestAppointment.get().getEmployee());  // ???????????? ??????????????? ??????.
                        latestAppointment.get().updateEndDate(appointment.getStartDate().minusDays(1)); // ????????? ???????????? ????????? -1???
                    }
                }

                appointment.getTransOrg().getEmployees().add(appointment.getEmployee());   // ????????? ?????? ?????????????????? ??????
                employee.updateOrganization(appointment.getTransOrg());  // ?????? ????????????
            } else {
                appointment.updateApprovementStatus(ApprovementStatus.APPR); // ?????? ??????

                /* ?????? ??? ?????? - ?????? ??????????????? ???????????? ???????????? -1???
                 * ?????? ???????????? ?????? null??? ????????????.
                 * ?????? ??????????????? ?????? ??????
                 */
                if (appointment.getAppointmentType().equals(AppointmentType.RETIRE)) {
                    // ??????????????? ??????????????? ????????????
                    Employee employee = appointment.getEmployee();

                    employee.updatRetireDate(LocalDate.now());
                    Optional<Appointment> latestAppointment = appointmentRepository.findLatestApp(appointment.getEmployee().getId()); // ?????? ????????? ????????????.

                    if (!latestAppointment.isEmpty()) {
                        latestAppointment.get().updateEndDate(LocalDate.now().minusDays(1)); // ????????? ???????????? ????????? -1???
                    }
                    // ?????? ??????
                    employee.updateOrganization(null);
                    latestAppointment.get().getTransOrg().getEmployees().remove(employee);
                }
            }
        }
        return appointment;
    }
}
