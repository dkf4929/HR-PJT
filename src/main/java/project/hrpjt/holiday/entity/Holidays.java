package project.hrpjt.holiday.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Holidays {
    @Id
    @GeneratedValue
    @Column(name = "holiday_id")
    private Long id;

    @Column(nullable = false)
    private int year;

    @Column(unique = true, nullable = false)
    private LocalDate holiday;

    private String reason;

    @Builder
    public Holidays(LocalDate holiday, String reason) {
        this.year = holiday.getYear();
        this.holiday = holiday;
        this.reason = reason;
    }
}
