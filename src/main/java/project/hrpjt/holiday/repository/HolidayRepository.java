package project.hrpjt.holiday.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.hrpjt.holiday.entity.Holidays;

public interface HolidayRepository extends JpaRepository<Holidays, Long>, HolidayRepositoryCustom {
}
