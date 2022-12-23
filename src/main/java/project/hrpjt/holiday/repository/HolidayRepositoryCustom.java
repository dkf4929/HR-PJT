package project.hrpjt.holiday.repository;

import project.hrpjt.holiday.entity.Holidays;

import java.util.List;

public interface HolidayRepositoryCustom {
    public List<Holidays> findByYears(List<Integer> years);
}
