package project.hrpjt.dayoffapply.repository;

import project.hrpjt.dayoffapply.entity.DayOffApply;

import java.util.List;

public interface DayOffApplyRepositoryCustom {
    public List<DayOffApply> findByEmpId(Long empId);

    public List<DayOffApply> findByOrgIds(List<Long> orgIds);

    public List<DayOffApply> findApprListByOrgIds(List<Long> orgIds);

    public List<DayOffApply> findApprListCEO();
}
