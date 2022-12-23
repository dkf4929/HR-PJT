package project.hrpjt.dayoff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.hrpjt.dayoff.entity.DayOff;

public interface DayOffRepository extends JpaRepository<DayOff, Long> {
}
