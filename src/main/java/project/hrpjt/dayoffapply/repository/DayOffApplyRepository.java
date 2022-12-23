package project.hrpjt.dayoffapply.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.hrpjt.dayoffapply.entity.DayOffApply;

public interface DayOffApplyRepository extends JpaRepository<DayOffApply, Long>, DayOffApplyRepositoryCustom {
}
