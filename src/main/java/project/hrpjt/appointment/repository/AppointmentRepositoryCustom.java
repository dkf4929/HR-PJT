package project.hrpjt.appointment.repository;

import project.hrpjt.appointment.dto.AppointmentFindDto;
import project.hrpjt.appointment.entity.Appointment;
import project.hrpjt.employee.entity.Employee;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepositoryCustom {
    public List<AppointmentFindDto> findAllByParam(String empNo, String empNm);

    public List<AppointmentFindDto> findMyAppointment(Long empId);

    public List<Appointment> findAllLeave(Long empId);

    public Optional<Appointment> findByIdFetch(Long id);

    public Optional<Appointment> findLatestApp(Long empId);

    public List<AppointmentFindDto> findAllApprList(Employee employee);

    public Long findOrgCount(Long id);
}
