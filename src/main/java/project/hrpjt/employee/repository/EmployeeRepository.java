package project.hrpjt.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.hrpjt.employee.entity.Employee;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    public Optional<Employee> findByemployeeNo(String employeeNo);

    public Optional<Employee> findByKakaoId(String id);

    public Optional<Employee> findByKakaoMail(String mail);

}