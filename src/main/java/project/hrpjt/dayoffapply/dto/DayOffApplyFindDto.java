package project.hrpjt.dayoffapply.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.hrpjt.appointment.entity.enumeration.ApprovementStatus;
import project.hrpjt.dayoffapply.entity.enumeration.DayOffType;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayOffApplyFindDto {
    private DayOffType dayOffType;
    private LocalDate startDate;
    private LocalDate endDate;
    private double minusDays;
    private ApprovementStatus status;
}
