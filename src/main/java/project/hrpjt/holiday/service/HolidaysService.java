package project.hrpjt.holiday.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.holiday.dto.HolidaysSaveDto;
import project.hrpjt.holiday.entity.Holidays;
import project.hrpjt.holiday.repository.HolidayRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class HolidaysService {
    private final HolidayRepository holidayRepository;

    public Holidays save(HolidaysSaveDto param) {
        return holidayRepository.save(Holidays.builder()
                .holiday(param.getHoliday())
                .reason(param.getReason())
                .build());
    }
}
