package project.hrpjt.appointment.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import project.hrpjt.appointment.entity.enumeration.ApprovementStatus;
import project.hrpjt.appointment.entity.enumeration.AppointmentType;
import project.hrpjt.organization.entity.Organization;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class AppointmentFindDto {
    @JsonIgnore
    private Long appointmentId;

    private AppointmentType appointmentType;
    private ApprovementStatus approvementStatus;
    private String empNo;
    private String empNm;
    private String transOrgNo;
    private String transOrgNm;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    public AppointmentFindDto(Long appointmentId, AppointmentType appointmentType, ApprovementStatus approvementStatus, String empNo, String empNm, Organization transOrg, LocalDate startDate, LocalDate endDate) {
        this.appointmentId = appointmentId;
        this.appointmentType = appointmentType;
        this.approvementStatus = approvementStatus;
        this.empNo = empNo;
        this.empNm = empNm;
        if (transOrg != null) {
            this.transOrgNo = transOrg.getOrgNo();
            this.transOrgNm = transOrg.getOrgNm();
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
