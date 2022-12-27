package project.hrpjt.dayoff.repository;

import project.hrpjt.dayoff.entity.DayOff;
import project.hrpjt.employee.entity.Employee;

import java.util.List;
import java.util.Optional;

public interface DayOffRepositoryCustom {
    public List<DayOff> findMyDayOff(Employee employee, List<Integer> years);

    public Optional<DayOff> findDayOff(Employee employee, int year);

    public List<DayOff> findUseTarget();
}
