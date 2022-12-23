package project.hrpjt.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.hrpjt.attendance.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
