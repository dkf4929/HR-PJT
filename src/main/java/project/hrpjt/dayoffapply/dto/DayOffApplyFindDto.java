package project.hrpjt.dayoffapply.dto;

import lombok.*;
import project.hrpjt.appointment.entity.enumeration.ApprovementStatus;
import project.hrpjt.dayoffapply.entity.enumeration.DayOffType;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class DayOffApplyFindDto {
    private DayOffType dayOffType;
    private LocalDate startDate;
    private LocalDate endDate;
    private double minusDays;
    private ApprovementStatus status;
}
