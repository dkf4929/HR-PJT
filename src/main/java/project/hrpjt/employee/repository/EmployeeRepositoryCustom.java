package project.hrpjt.employee.repository;

import project.hrpjt.employee.entity.Employee;
import project.hrpjt.organization.entity.Organization;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepositoryCustom {
    public Long countInOfficeEmp(Organization organization);

    public List<Employee> findAllFetch();

    public Optional<Employee> findByIdFetch(Long empId);
}
