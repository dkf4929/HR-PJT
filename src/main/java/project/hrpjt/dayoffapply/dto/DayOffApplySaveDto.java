package project.hrpjt.dayoffapply.dto;

import lombok.*;
import project.hrpjt.dayoffapply.entity.enumeration.DayOffType;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class DayOffApplySaveDto {
    private DayOffType dayOffType;
    private LocalDate startDate;
    private LocalDate endDate;
}
