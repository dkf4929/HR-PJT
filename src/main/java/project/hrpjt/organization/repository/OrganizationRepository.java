package project.hrpjt.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.hrpjt.organization.entity.Organization;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long>, OrganizationRepositoryCustom {
}
