package project.hrpjt.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.hrpjt.appointment.entity.enumeration.ApprovementStatus;
import project.hrpjt.appointment.entity.enumeration.AppointmentType;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentSaveDto {
    private AppointmentType type;
    private ApprovementStatus status;   // testdata용
    private String empNo;
    private String orgNo;
    private LocalDate startDate;
    private LocalDate endDate;
}
