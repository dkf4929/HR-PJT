package project.hrpjt.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.hrpjt.appointment.entity.enumeration.AppointmentStatus;
import project.hrpjt.appointment.entity.enumeration.AppointmentType;
import project.hrpjt.organization.entity.Organization;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class AppointmentFindDto {
    private AppointmentType appointmentType;
    private AppointmentStatus appointmentStatus;
    private String empNo;
    private String empNm;
    private String transOrgNo;
    private String transOrgNm;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    public AppointmentFindDto(AppointmentType appointmentType, AppointmentStatus appointmentStatus, String empNo, String empNm, Organization transOrg, LocalDate startDate, LocalDate endDate) {
        this.appointmentType = appointmentType;
        this.appointmentStatus = appointmentStatus;
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
