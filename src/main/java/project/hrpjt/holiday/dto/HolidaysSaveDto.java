package project.hrpjt.holiday.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HolidaysSaveDto {
    private LocalDate holiday;
    private String reason;
}
