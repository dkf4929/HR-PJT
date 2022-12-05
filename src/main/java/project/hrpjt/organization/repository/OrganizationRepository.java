package project.hrpjt.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.hrpjt.organization.entity.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
