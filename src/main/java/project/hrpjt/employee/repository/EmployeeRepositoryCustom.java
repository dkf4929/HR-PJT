package project.hrpjt.employee.repository;

import project.hrpjt.organization.entity.Organization;

public interface EmployeeRepositoryCustom {
    public Long countInOfficeEmp(Organization organization);
}
